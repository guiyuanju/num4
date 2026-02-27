#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QTextEdit>
#include <QLineEdit>
#include <QPushButton>
#include <QLabel>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:
    void onGenerateClicked();
    void onClearClicked();
    void onCopyClicked();

private:
    QString processText(const QString& input, int m);
    
    QTextEdit* inputBox;
    QTextEdit* outputBox;
    QLineEdit* mEntry;
    QPushButton* generateBtn;
    QPushButton* clearBtn;
    QPushButton* copyBtn;
};

#endif // MAINWINDOW_H
