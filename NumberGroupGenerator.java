import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import java.util.List;

public class NumberGroupGenerator {

    // =================
    // 核心处理逻辑
    // =================
    public static String process(String input, int m) throws Exception {
        String[] rawLines = input.split("\\r?\\n");
        List<String> lines = new ArrayList<>();
        for (String l : rawLines) {
            l = l.trim();
            if (!l.isEmpty()) lines.add(l);
        }

        if (m <= 1) throw new Exception("M must > 1");
        if (lines.size() % m != 0) throw new Exception("line count must be multiple of M");

        List<String> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += m) {
            List<String> group = lines.subList(i, i + m);
            String last = group.get(m - 1);
            for (int j = 0; j < m - 1; j++) {
                result.add(group.get(j));
                result.add(last);
                result.add("");
            }
        }

        return String.join("\n", result);
    }

    // =================
    // 快捷键支持
    // =================
    private static void enableDefaultKeyBindings(JTextComponent textComponent) {
        int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        textComponent.getInputMap().put(KeyStroke.getKeyStroke('C', shortcutKey), DefaultEditorKit.copyAction);
        textComponent.getInputMap().put(KeyStroke.getKeyStroke('V', shortcutKey), DefaultEditorKit.pasteAction);
        textComponent.getInputMap().put(KeyStroke.getKeyStroke('X', shortcutKey), DefaultEditorKit.cutAction);
        textComponent.getInputMap().put(KeyStroke.getKeyStroke('A', shortcutKey), DefaultEditorKit.selectAllAction);
    }

    // =================
    // 右键菜单支持
    // =================
    private static void addPopupMenu(JTextComponent textComponent) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("剪切");
        menu.add(cut);

        JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("复制");
        menu.add(copy);

        JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("粘贴");
        menu.add(paste);

        textComponent.setComponentPopupMenu(menu);
    }

    // =================
    // GUI
    // =================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Number Group Generator");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 主面板
            JPanel central = new JPanel(new BorderLayout());

            // 输入输出区
            JTextArea inputBox = new JTextArea();
            inputBox.setLineWrap(false);
            inputBox.setBorder(BorderFactory.createTitledBorder("输入多行数字..."));

            JTextArea outputBox = new JTextArea();
            outputBox.setLineWrap(false);
            outputBox.setEditable(false);
            outputBox.setBorder(BorderFactory.createTitledBorder("输出"));

            // 启用快捷键 & 右键菜单
            enableDefaultKeyBindings(inputBox);
            enableDefaultKeyBindings(outputBox);
            addPopupMenu(inputBox);
            addPopupMenu(outputBox);

            JScrollPane inputScroll = new JScrollPane(inputBox);
            JScrollPane outputScroll = new JScrollPane(outputBox);

            JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputScroll, outputScroll);
            splitter.setResizeWeight(0.5);

            central.add(splitter, BorderLayout.CENTER);

            // 控制栏
            JPanel controlBar = new JPanel();
            JLabel mLabel = new JLabel("每组行数:");
            JTextField mEntry = new JTextField(5);
            JButton generateBtn = new JButton("生成");
            JButton clearBtn = new JButton("清除");
            JButton copyBtn = new JButton("复制");

            controlBar.add(mLabel);
            controlBar.add(mEntry);
            controlBar.add(Box.createHorizontalGlue());
            controlBar.add(generateBtn);
            controlBar.add(clearBtn);
            controlBar.add(copyBtn);

            central.add(controlBar, BorderLayout.SOUTH);

            // 按钮逻辑
            generateBtn.addActionListener(e -> {
                int m;
                try {
                    m = Integer.parseInt(mEntry.getText().trim());
                } catch (NumberFormatException ex) {
                    outputBox.setText("invalid M");
                    return;
                }

                try {
                    String out = process(inputBox.getText(), m);
                    outputBox.setText(out);
                } catch (Exception ex) {
                    outputBox.setText(ex.getMessage());
                }
            });

            clearBtn.addActionListener(e -> {
                inputBox.setText("");
                outputBox.setText("");
                mEntry.setText("");
            });

            copyBtn.addActionListener(e -> {
                StringSelection selection = new StringSelection(outputBox.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            });

            window.setContentPane(central);
            window.setSize(400, 180);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}