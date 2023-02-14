const {Editor} = toastui;
const {codeSyntaxHighlight} = Editor.plugin;

const editor = new toastui.Editor({
    el: document.querySelector('.js-toast-editor'),
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

$(window).resize(() => {
    scalingEditorHeight();
})

function scalingEditorHeight() {
    const viewHeight = window.innerHeight;
    const height = (viewHeight - 170) + "px";
    editor.setHeight(height);
}