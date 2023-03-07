function registerPost() {
    save("/api/board/" + groupUrl + "/new");
}

function saveTemporarily() {
    save("/api/board/" + groupUrl + "/temp", true);
}

function moveToPreviousPage() {
    history.go(-1);
}

//태그 입력 시 폭 자동조절
$(document).on('keydown', '.tag', (e) => {
    const $target = $(e.target);
    const value = $target.val();
    $("#tags").append("<div id='virtual_dom'>" + value + "</div>");

    const $virtualDom = $('#virtual_dom');
    const calWidth = $virtualDom.width() + 15;
    $virtualDom.remove();

    $target.css("width", calWidth);
})

function tempSaveModalToggle() {
    $(".modal-portal").toggle("fast", "linear");
}

let isOverwrite;

function save(url, temp) {
    const $category = $("#js-categoryOption option:selected");
    const request = {
        title: $("#js-title").val(),
        description: $("#js-description").val(),
        groupUrl: groupUrl,
        categoryId: $category.data("category-id"),
        categoryName: $category.val(),
        status: $("#js-status option:selected").val(),
        tags: [],
        content: {
            html: ckEditor5.getData()
        }
    }

    $('.tag').each((idx, tag) => {
        if (tag.value !== '') request.tags.push(tag.value);
    })

    //temp라면 id가 있을 시 id 추가
    if (temp && isOverwrite) {
        request.id = isOverwrite;
    }

    post(url, request)
        .then(res => res.text())
        .then(boardId => {
            if (temp) {
                addTempSaveList(request, boardId);
                alert("임시 저장을 완료했습니다!");
            } else {
                location.href = "./" + boardId;
            }
        })
        .catch(error => {
            alert("컨텐츠 저장에 실패했습니다!");
            console.error(error);
        });
}

function addTempSaveList(request, boardId) {
    if (overwrite(boardId, request.title)) return;

    //처음 저장됨
    const $newElement = $(`<li class="temp-save-element" id="${boardId}">
            <span class="temp-save-date">방금 전</span>
            <span class="temp-save-title">${request.title}</span>
        </li>`);

    $(".temp-save-wrapper > ul").append($newElement);

    const $count = $("#temp-save-count");
    const newCount = parseInt($count.text()) + 1;
    $count.text(newCount);
}

function overwrite(boardId, title) {
    const $post = $("#" + boardId);
    if ($post.length) {
        $post.children(".temp-save-date").text("방금 전");
        $post.children(".temp-save-title").text(title);
        isOverwrite = boardId;
        return true;
    }
}

//임시 저장글 불러오기
$(document).on('click', '.temp-save-element', (e) => {
    const $target = $(e.target);

    let tempPostId;
    if ($target.prop("tagName") !== "LI") {
        tempPostId = $target.parent().attr('id');
    }
    else {
        tempPostId = $(e.target).attr('id');
    }

    get("/api/board/temp/" + tempPostId)
        .then(res => res.json())
        .then(tempPost => {
            renderContent(tempPost);
            isOverwrite = tempPost.id;
            tempSaveModalToggle();
        })
        .catch(error => {
            alert("임시 저장글을 가져오는데 실패했습니다!");
            console.error(error);
        })
})

function renderContent(tempPost) {
    $("#js-title").val(tempPost.title);
    $("#js-description").val(tempPost.description);
    $("#js-categoryOption").val(tempPost.categoryName).prop("selected", true);
    $("#js-status").val(tempPost.status).prop("selected", true);
    renderTags(tempPost.tags);
    ckEditor5.setData(tempPost.content.html);
}

function renderTags(tags) {
    $(".tag-wrapper").remove();
    const $emptyTagBox = $("#tags > .empty");

    tags.forEach((tag) => {
        const $tagWrapper = $(`<div class="tag-wrapper flex-horizon-center"">
                <input class="tag" value="${tag}">
                <i class="pointer material-icons-outlined delete md-16 gray">delete</i>
            </div>`);

        $emptyTagBox.before($tagWrapper);
    })
}
