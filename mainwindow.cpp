#include "mainwindow.h"
#include <QWidget>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QSplitter>
#include <QFrame>
#include <QApplication>
#include <QClipboard>
#include <QMessageBox>
#include <QStringList>
#include <QRegularExpression>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    setWindowTitle("Number Group Generator");
    
    // Central widget
    QWidget* central = new QWidget(this);
    setCentralWidget(central);
    
    QVBoxLayout* mainLayout = new QVBoxLayout(central);
    
    // Splitter for input/output
    QSplitter* splitter = new QSplitter(Qt::Horizontal, this);
    
    // Input area
    inputBox = new QTextEdit(this);
    inputBox->setPlaceholderText("输入多行数字...");
    inputBox->setFrameStyle(QFrame::Box | QFrame::Plain);
    inputBox->setLineWidth(1);
    splitter->addWidget(inputBox);
    
    // Output area
    outputBox = new QTextEdit(this);
    outputBox->setReadOnly(true);
    outputBox->setPlaceholderText("输出");
    outputBox->setFrameStyle(QFrame::Box | QFrame::Plain);
    outputBox->setLineWidth(1);
    splitter->addWidget(outputBox);
    
    splitter->setStretchFactor(0, 1);
    splitter->setStretchFactor(1, 1);
    mainLayout->addWidget(splitter);
    
    // Control bar
    QWidget* controlBar = new QWidget(this);
    QHBoxLayout* controlLayout = new QHBoxLayout(controlBar);
    
    QLabel* mLabel = new QLabel("每组行数:", this);
    mEntry = new QLineEdit(this);
    mEntry->setMaximumWidth(80);
    
    generateBtn = new QPushButton("生成", this);
    clearBtn = new QPushButton("清除", this);
    copyBtn = new QPushButton("复制", this);
    
    controlLayout->addWidget(mLabel);
    controlLayout->addWidget(mEntry);
    controlLayout->addStretch();
    controlLayout->addWidget(generateBtn);
    controlLayout->addWidget(copyBtn);
    controlLayout->addWidget(clearBtn);
    
    mainLayout->addWidget(controlBar);
    
    // Connect signals
    connect(generateBtn, &QPushButton::clicked, this, &MainWindow::onGenerateClicked);
    connect(clearBtn, &QPushButton::clicked, this, &MainWindow::onClearClicked);
    connect(copyBtn, &QPushButton::clicked, this, &MainWindow::onCopyClicked);
    
    // Window size
    resize(300, 150);
}

MainWindow::~MainWindow()
{
}

QString MainWindow::processText(const QString& input, int m)
{
    if (m <= 1) {
        throw QString("每组行数应大于1");
    }
    
    // Split into lines and filter empty ones
    QStringList rawLines = input.split(QRegularExpression("\\r?\\n"));
    QStringList lines;
    for (const QString& line : rawLines) {
        QString trimmed = line.trimmed();
        if (!trimmed.isEmpty()) {
            lines.append(trimmed);
        }
    }
    
    if (lines.size() % m != 0) {
        throw QString("每组行数应当可以整除总行数");
    }
    
    QStringList result;
    for (int i = 0; i < lines.size(); i += m) {
        QString last = lines[i + m - 1];
        for (int j = 0; j < m - 1; j++) {
            result.append(lines[i + j]);
            result.append(last);
            result.append("");
        }
    }
    
    return result.join("\n");
}

void MainWindow::onGenerateClicked()
{
    bool ok;
    int m = mEntry->text().toInt(&ok);
    
    if (!ok) {
        outputBox->setText("请输入每组行数");
        return;
    }
    
    try {
        QString output = processText(inputBox->toPlainText(), m);
        outputBox->setText(output);
    } catch (const QString& error) {
        outputBox->setText(error);
    }
}

void MainWindow::onClearClicked()
{
    inputBox->clear();
    outputBox->clear();
    mEntry->clear();
}

void MainWindow::onCopyClicked()
{
    QClipboard* clipboard = QApplication::clipboard();
    clipboard->setText(outputBox->toPlainText());
}
