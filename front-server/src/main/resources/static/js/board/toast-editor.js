const {Editor} = toastui;
const {codeSyntaxHighlight} = Editor.plugin;

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

async function pageRender() {
    content_scroll_plugin();
    getMarkdown();
}

function content_scroll_plugin() {
    $(".scroll-section").mCustomScrollbar({
        theme: "minimal-dark",
        mouseWheelPixels: 300,
        scrollInertia: 300, // 부드러운 스크롤 효과 적용
    });
}

function getMarkdown() {
    const commentId = $('.js-toast-editor').attr('id');
    const url = "/api/board/content/" + commentId;

    get(url)
        .then(res => res.json())
        .then(contentDto => {
            editor.setMarkdown(contentDto.markdown);
        })
        .catch(error => {
            alert("컨텐츠를 불러오는데 실패했습니다!");
            console.error(error);
        })
}