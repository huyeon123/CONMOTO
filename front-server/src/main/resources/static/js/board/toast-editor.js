const {Editor} = toastui;
const {codeSyntaxHighlight} = Editor.plugin;
const initialHeight = window.outerHeight * 0.5;

const editor = new toastui.Editor({
    el: document.querySelector('.js-toast-editor'),
    height: '800px',
    initialEditType: 'markdown',
    previewStyle: 'tab',
    placeholder: "여기에 내용을 입력하세요.",
    language: 'ko-KR',
    plugins: [[codeSyntaxHighlight, {highlighter: Prism}]],
    customHTMLRenderer: {
        htmlBlock: {
            iframe(node) { //Youtube를 위한 iframe 허용 설정
                return [
                    {
                        type: 'openTag',
                        tagName: 'iframe',
                        outerNewLine: true,
                        attributes: node.attrs
                    },
                    {type: 'html', content: node.childrenHTML},
                    {type: 'closeTag', tagName: 'iframe', outerNewLine: true}
                ];
            }
        }
    }
});

$(function content_scroll_plugin() {
    $(".scroll-section").mCustomScrollbar({
        theme: "minimal-dark",
        mouseWheelPixels: 150,
        scrollInertia: 500, // 부드러운 스크롤 효과 적용
    });
});