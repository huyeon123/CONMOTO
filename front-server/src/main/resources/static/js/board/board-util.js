const curr = new Date();
const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
const KR_now = new Date(utc + (2 * KR_TIME_DIFF)); //서버에서 다시 KR_TIME_DIFF만큼 빠짐
let nextPage = 0;
let isFetching = false;

const getList = (where, request) => {
    isFetching = true;
    const url = "/api/board/latest/" + where;
    return post(url, request);
};

function createBoard() {
    const url = "/api/board/" + groupUrl + "/create";
    get(url)
        .then(res => {
            if (canGetData(res)) {
                getJson(res)
                    .then(id => {
                        alert("게시글을 생성했습니다.");
                        window.location.href = "/workspace/" + groupUrl + "/board/" + id;
                    })
            }
        });
}

function content_scroll_plugin(getListCallback) {
    $(".scroll-section").mCustomScrollbar({
        theme: "minimal-dark",
        mouseWheelPixels: 150,
        scrollInertia: 300, // 부드러운 스크롤 효과 적용
        callbacks: {
            onTotalScroll: function () {
                console.log("BOTTOM!")
                getListCallback();
            },
            onTotalScrollOffset: 100,
            alwaysTriggerOffsets: false,
        }
    });
}

function drawBoards(data) {
    data.forEach((item, index) => {
        drawBoard(item);

        setNextPageIfEnd(index, data);
    });
    isFetching = false;
}

function drawBoard(item) {
    const elementId = drawVDOM(item);
    drawPreview(elementId, item);
    drawBoardInfo(elementId, item);
}

function drawVDOM(item) {
    const virtualDom = `<div id="board_${item.id}" class="board-area">
        <a href="${item.url}" class="board-anchor">
            <div class="board-card">
                <div class="board-preview"></div>
                <div class="board-title"></div>
                <div class="board-description"></div>
                <div style="padding: 0 10px 0;">
                    <div class="board-status"></div>
                </div>
                <div class="board-signature">
                    <div class="board-updatedAt"></div>
                    <div class="board-author"></div>
                </div>
            </div>
        </a>
    </div>`;

    $('.content-body').append(virtualDom);
    return '#board_' + item.id;
}

function drawPreview(elementId, item) {
    const viewer = new Editor.factory(viewerConfig(elementId));
    //TODO: Mongo DB 연결되면 item.markdown으로 바꾸기
    let markdown = "";
    item.contents.forEach(element => {
        markdown += element.content + "\n";
    })
    viewer.setMarkdown(markdown);
}

const {Editor} = toastui;

function viewerConfig(elementId) {
    const {codeSyntaxHighlight} = Editor.plugin;

    return {
        el: document.querySelector(elementId + ' .board-preview'),
        viewer: true,
        language: 'ko-KR',
        placeholder: '작성된 내용이 없습니다.',
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
    };
}

function drawBoardInfo(elementId, item) {
    addTitle(elementId, item.title);
    addDescription(elementId, item.description);
    addStatus(elementId, item.status);
    addAuthor(elementId, item.author);
    addUpdateTime(elementId, item.updatedAt);
}

function addTitle(elementId, title) {
    $(elementId + ' .board-title').text(title);
}

function addDescription(elementId, description) {
    if (description !== null) {
        $(elementId + ' .board-description').text(description);
    }
}

function addStatus(elementId, status) {
    const $boardStatus = $(elementId + ' .board-status')
    $boardStatus.text(status);
    $boardStatus.addClass(status.toLowerCase());
}

function addAuthor(elementId, author) {
    $(elementId + ' .board-author').text(author);
}

function addUpdateTime(elementId, updatedAt) {
    $(elementId + ' .board-updatedAt').text(updatedAt);
}

function setNextPageIfEnd(index, data) {
    if (index === data.length - 1) {
        nextPage++;
    }
}

function moveToMyPage() {
    location.href = "./info";
}

function moveToMyBoard() {
    location.href = "./board";
}

function moveToMyComment() {
    location.href = "./comment";
}

function moveToResign() {
    location.href = "./resign";
}