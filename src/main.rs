use iced::widget::{button, column, container, row, text, text_editor, text_input};
use iced::{Center, Element, Fill, Size, Task};

fn main() -> iced::Result {
    iced::application(
        || (NumberGroupGenerator::default(), Task::none()),
        NumberGroupGenerator::update,
        NumberGroupGenerator::view,
    )
    .window_size(Size::new(400.0, 200.0))
    .run()
}

// =================
// Application State
// =================
#[derive(Debug)]
struct NumberGroupGenerator {
    input_content: text_editor::Content,
    output_content: text_editor::Content,
    m_value: String,
}

#[derive(Debug, Clone)]
enum Message {
    InputAction(text_editor::Action),
    OutputAction(text_editor::Action),
    MValueChanged(String),
    Generate,
    Clear,
    Copy,
}

impl Default for NumberGroupGenerator {
    fn default() -> Self {
        Self {
            input_content: text_editor::Content::new(),
            output_content: text_editor::Content::new(),
            m_value: String::new(),
        }
    }
}

impl NumberGroupGenerator {
    fn update(&mut self, message: Message) -> Task<Message> {
        match message {
            Message::InputAction(action) => {
                self.input_content.perform(action);
                Task::none()
            }
            Message::OutputAction(action) => {
                self.output_content.perform(action);
                Task::none()
            }
            Message::MValueChanged(value) => {
                self.m_value = value;
                Task::none()
            }
            Message::Generate => {
                let m = match self.m_value.trim().parse::<usize>() {
                    Ok(val) => val,
                    Err(_) => {
                        self.output_content = text_editor::Content::with_text("请输入每组行数");
                        return Task::none();
                    }
                };

                let input = self.input_content.text();
                match process(&input, m) {
                    Ok(result) => {
                        self.output_content = text_editor::Content::with_text(&result);
                    }
                    Err(e) => {
                        self.output_content = text_editor::Content::with_text(&e);
                    }
                }
                Task::none()
            }
            Message::Clear => {
                self.input_content = text_editor::Content::new();
                self.output_content = text_editor::Content::new();
                self.m_value.clear();
                Task::none()
            }
            Message::Copy => {
                let output_text = self.output_content.text();
                if let Ok(mut clipboard) = arboard::Clipboard::new() {
                    let _ = clipboard.set_text(&output_text);
                }
                Task::none()
            }
        }
    }

    fn view(&self) -> Element<'_, Message> {
        let input_editor = text_editor(&self.input_content)
            .on_action(Message::InputAction)
            .height(Fill);

        let output_editor = text_editor(&self.output_content)
            .on_action(Message::OutputAction)
            .height(Fill);

        let content_row = row![
            container(column![text("输入").size(10), input_editor].spacing(3))
                .width(Fill)
                .height(Fill)
                .padding(4)
                .style(container::bordered_box),
            container(column![text("输出").size(10), output_editor].spacing(3))
                .width(Fill)
                .height(Fill)
                .padding(4)
                .style(container::bordered_box),
        ]
        .spacing(8)
        .height(Fill);

        let m_input = text_input("", &self.m_value)
            .on_input(Message::MValueChanged)
            .size(10)
            .width(60);

        let control_bar = row![
            text("每组行数:").size(10).align_y(Center),
            m_input,
            button(text("生成").size(10)).on_press(Message::Generate),
            button(text("复制").size(10)).on_press(Message::Copy),
            button(text("清除").size(10)).on_press(Message::Clear),
        ]
        .spacing(6)
        .align_y(Center);

        let main_column = column![content_row, container(control_bar).padding(6),].height(Fill);

        container(main_column)
            .padding(6)
            .width(Fill)
            .height(Fill)
            .into()
    }
}

// =================
// Core Processing Logic
// =================
fn process(input: &str, m: usize) -> Result<String, String> {
    let lines: Vec<String> = input
        .lines()
        .map(|l| l.trim().to_string())
        .filter(|l| !l.is_empty())
        .collect();

    if m <= 1 {
        return Err("每组行数应当大于1".to_string());
    }

    if lines.len() % m != 0 {
        return Err("总行数应当是每组行数的倍数".to_string());
    }

    let mut result = Vec::new();

    for chunk in lines.chunks(m) {
        let last = &chunk[m - 1];
        for i in 0..m - 1 {
            result.push(chunk[i].clone());
            result.push(last.clone());
            result.push(String::new());
        }
    }

    Ok(result.join("\n"))
}
