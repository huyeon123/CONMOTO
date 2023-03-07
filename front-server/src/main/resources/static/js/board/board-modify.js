const boardId = pathname.substring(pathname.lastIndexOf("/") + 1);

async function pageRender() {
    getHTML();
    autoScalingWidth("input.tag", "#tags", 15);
}

function getHTML() {
    const contentId = $('#ck5-editor').data('content-id');
    const url = "/api/board/content/" + contentId;

    get(url)
        .then(res => res.json())
        .then(contentDto => {
            if (contentDto.html != null) ckEditor5.setData(contentDto.html);
        })
        .catch(error => {
            alert("컨텐츠를 불러오는데 실패했습니다!");
            console.error(error);
        })
}

function autoScalingWidth(elementSelector, containerSelector, extraSize) {
    $(elementSelector).not('.empty').each((idx, element) => {
        const value = element.value;
        $(containerSelector).append("<div id='virtual_dom'>" + value + "</div>");

        const $virtualDom = $('#virtual_dom');
        const calWidth = $virtualDom.width() + extraSize;
        $virtualDom.remove();

        $(element).css("width", calWidth);
    })
}

/*header*/
$(document).on('blur', '#js-title', (e) => {
    const url = "/api/board/edit/title";
    const request = {
        title: $(e.target).val(),
    };
    saveBoard(url, request);
})

$(document).on('blur', '#js-description', (e) => {
    const url = "/api/board/edit/description";
    const request = {
        description: $(e.target).val(),
    };
    saveBoard(url, request);
})

$(document).on('change', '#js-status', () => {
    const url = "/api/board/edit/status";
    const request = {
        status: $("#js-status option:selected").val(),
    };
    saveBoard(url, request);
})

$(document).on('change', '#js-categoryOption', () => {
    const url = "/api/board/edit/category";
    const $option = $("#js-categoryOption option:selected");
    const categoryName = $option.val();
    const categoryId = $option.attr("id");

    if (categoryName === "카테고리 미선택") return;

    const request = {
        categoryId: categoryId,
        categoryName: categoryName
    };
    saveBoard(url, request);
})

function saveBoard(url, request) {
    request.id = boardId;

    put(url, request)
        .catch((error) => {
            alert("저장에 실패했습니다!");
            console.error(error);
        })
}

function deleteBoard() {
    if (!confirm("정말로 삭제하시겠습니까?")) {
        return;
    }

    delWithoutBody("/api/board/" + boardId)
        .then(() => {
            location.href = "/community/" + groupUrl;
        })
        .catch(error => {
            alert("삭제에 실패했습니다!");
            console.error(error);
        });
}

$(document).on('keydown', '.tag', (e) => {
    const $target = $(e.target);
    const value = $target.val();
    $("#tags").append("<div id='virtual_dom'>" + value + "</div>");

    const $virtualDom = $('#virtual_dom');
    const calWidth = $virtualDom.width() + 15;
    $virtualDom.remove();

    $target.css("width", calWidth);
})

//태그 삭제버튼 클릭 시 남아있는 태그들 순서대로 저장
$(document).on('click', '#tags i.delete', (e) => {
    if (!confirm("태그를 삭제하시겠습니까?")) return;

    $(e.target).parent().remove();
    saveTags();
})

//태그 입력 후 엔터시 저장
$(document).on('keyup', '.tag', (e) => {
    if (e.keyCode === 13) {
        saveTags();
    }
});

function saveTags() {
    const url = "/api/board/edit/tags";
    const request = {
        id: boardId,
        tags: []
    };

    $('.tag').each((idx, tag) => {
        if (tag.value !== '') request.tags.push(tag.value);
    })

    put(url, request)
        .then((res) => {
            if (res.ok) {
                location.reload();
            }
        })
        .catch((error) => {
            alert("태그 저장에 실패했습니다!");
            console.error(error);
        });
}

function moveToPreviousPage() {
    history.go(-1);
}

function registerContent() {
    const url = "/api/board/edit/content";
    const request = {
        boardId: boardId,
        markdown: ckEditor5.getData()
    }

    put(url, request)
        .then((res) => {
            if (res.ok) location.href = "../" + boardId;
        })
        .catch(error => {
            alert("컨텐츠 저장에 실패했습니다!");
            console.error(error);
        });
}