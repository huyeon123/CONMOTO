$(() => {
    $("#sortable")
        .sortable({
            axis: "y",
            containment: "parent",
            handle: "i",
            cursor: "move",
            tolerance: "pointer",
            placeholder: "ui-state-highlight"
        })
        .disableSelection();
})

function pageRender() {
    fetchGrade();
}

//그룹 등급 정보 불러오기
let levelList;
function fetchGrade() {
    const url = "/api/group/grade?url=" + groupUrl;
    get(url)
        .then(res => res.json())
        .then(data => levelList = data.levelList);
}

//카테고리 정보 전체 저장
function saveCategory() {
    const url = "/api/category/save?url=" + groupUrl;
    const request = [];

    const $categoryItem = $("#sortable").children();

    $categoryItem.each((index, item) => {
        const $item = $(item);
        const requestElem = {
            groupUrl: groupUrl,
            name: $($item.find(".js-category-title")).text(),
            description: $item.attr("data-description"),
            type: $item.attr("data-category-type")
        }

        if (requestElem.type === "CATEGORY_GROUP") {
            requestElem.fold = $($item.find("input:checkbox")).is(":checked");
        } else {
            requestElem.indent = $item.attr("data-indent") === "true";
            requestElem.availableWriteLevel = $item.attr("data-write-level");
            requestElem.availableCommentLevel = $item.attr("data-comment-level");
            requestElem.availableReadLevel = $item.attr("data-read-level");
        }

        request.push(requestElem);
    })

    post(url, request)
        .then((res) => {
            if (res.ok) {
                alert("정상 반영되었습니다.");
                location.href = "/community/" + groupUrl;
            }
        })
        .catch(error => {
            alert("저장에 실패했습니다!");
            console.error(error)
        });
}

//추가 버튼 클릭 시 모달창 열기
function openAddModal() {
    $("#modal-dimmed").show();
    createNewCategoryModalHTML();
}

//카테고리 추가 시 모달창 정보 생성
function createNewCategoryModalHTML() {
    const item = {
        name: "",
        description: "",
        writeLevel: 0,
        commentLevel: 0,
        readLevel: 0,
        indent: false,
        type: "BASIC",
        new: true
    }
    createModalHTML(item);
    createBasicCategoryDetailHTML(item);
    registerFormChanger(item);
}

//모달창 필수 form 생성
function createModalHTML(item) {
    const $element = $(`
        <div class="custom-modal-wrapper">
            <div class="custom-modal-inner">
                <div class="custom-modal-title">카테고리 정보를 입력해주세요.</div>
                <div class="custom-modal-main">
                    <div class="custom-modal-main-elem">
                        <label>타입</label>
                        <label for="basic-category">일반 카테고리</label>
                        <input id="basic-category" class="group-menu-radio" type="radio" name="category-type" value="BASIC">
                        <label for="group-category">카테고리 그룹</label>
                        <input id="group-category" class="group-menu-radio" type="radio" name="category-type" value="CATEGORY_GROUP">
                    </div>
                    <div id="category-modal-detail"></div>
                </div>
                <div id="modal-footer" class="flex-end">
                    <div>
                        <button id="category-modal-cancel" class="manage-menu-button warn" onclick="closeModal()">취소</button>
                        <button id="category-modal-save" class="manage-menu-button default" onclick="adaptCategoryHTML(${item.new}, ${item.index})">적용</button>
                    </div>
                </div>
            </div>
        </div>
    `);

    $("#modal").append($element);
    //타입 체크
    $(`.group-menu-radio[value=${item.type}]`).prop("checked", true);
    //삭제 버튼 추가
    const $modalFooter = $("#modal-footer");
    $modalFooter.attr("class", "flex-space-between");
    $modalFooter.prepend(`
        <div>
            <button id="category-modal-delete" class="manage-menu-button warn" onclick="deleteCategory(${item.index})">삭제</button>
        </div>
    `);
}

//카테고리 삭제
function deleteCategory(index) {
    $($("#sortable").children().get(index)).remove();
    closeModal();
}

//타입 변경 시 그에 맞게 form 변경
function registerFormChanger(item) {
    $("#basic-category").click(() => {
        $("#category-modal-detail").children().remove();
        createBasicCategoryDetailHTML(item);
    })
    $("#group-category").click(() => {
        $("#category-modal-detail").children().remove();
        createCategoryGroupDetailHTML(item);
    })
}

//일반 카테고리 form 생성
function createBasicCategoryDetailHTML(item) {
    const $element = $(`
        <div class="custom-modal-main-elem">
            <input id="form-category-name" class="form-control" type="text" placeholder="카테고리명을 입력해주세요." value="${item.name}">
        </div>  
        <div class="custom-modal-main-elem">
            <input id="form-category-description" class="form-control" type="text" placeholder="카테고리를 설명해주세요." value="${item.description}">
        </div>   
        <div class="custom-modal-main-elem">
            <div class="custom-modal-elem-wrapper">
                <div class="custom-modal-main-elem-title">글쓰기 허용 등급</div>
                <div id="write-level" class="group-custom-select modal">
                    <span>${levelList[item.writeLevel].gradeName}</span>
                    <i class="material-icons-outlined md-16">unfold_more</i>
                </div>
                <ul id="write-level-list" class="custom-option-wrapper">
                    <div class="custom-option-inner"></div>
                </ul>
            </div>
        </div>   
        <div class="custom-modal-main-elem">
            <div class="custom-modal-elem-wrapper">
                <div class="custom-modal-main-elem-title">댓글 허용 등급</div>
                <div id="comment-level" class="group-custom-select modal">
                    <span>${levelList[item.commentLevel].gradeName}</span>
                    <i class="material-icons-outlined md-16">unfold_more</i>
                </div>
                <ul id="comment-level-list" class="custom-option-wrapper">
                    <div class="custom-option-inner"></div>
                </ul>
            </div>
        </div>   
        <div class="custom-modal-main-elem">
            <div class="custom-modal-elem-wrapper">
                <div class="custom-modal-main-elem-title">댓글 허용 등급</div>
                <div id="read-level" class="group-custom-select modal">
                    <span>${levelList[item.readLevel].gradeName}</span>
                    <i class="material-icons-outlined md-16">unfold_more</i>
                </div>
                <ul id="read-level-list" class="custom-option-wrapper">
                    <div class="custom-option-inner"></div>
                </ul>
            </div>   
        </div>
        <div class="custom-modal-main-elem">
            <label for="form-category-indent">들여쓰기</label>
            <input id="form-category-indent" type="checkbox">
        </div>   
    `);

    $("#category-modal-detail").append($element);
    $("#form-category-indent").prop("checked", item.indent);
    $("#write-level").attr("data-level", item.writeLevel);
    $("#comment-level").attr("data-level", item.commentLevel);
    $("#read-level").attr("data-level", item.readLevel);
    renderGradeOption();
    registerDetailListener();
}

//카테고리 그룹 form 생성
function createCategoryGroupDetailHTML(item) {
    const $element = $(`
        <div class="custom-modal-main-elem">
            <input id="form-category-name" class="form-control" type="text" placeholder="그룹 제목을 입력해주세요." value="${item.name}">
        </div>  
        <div class="custom-modal-main-elem">
            <input id="form-category-description" class="form-control" type="text" placeholder="그룹을 설명해주세요." value="${item.description}">
        </div> 
        <div class="custom-modal-main-elem">
            <label for="form-category-fold">접기 여부</label>
            <input id="form-category-fold" type="checkbox">
        </div>  
    `);

    $("#category-modal-detail").append($element);
    $(`#form-category-fold`).prop("checked", item.fold);
}

//모달창에서 사용하는 이벤트리스너 등록
function registerDetailListener() {
    //선택한 select의 option만 열리게 리스너 설정
    $(".group-custom-select.modal").click((e) => {
        e.stopPropagation();
        const $target = $($(e.target).parents(".custom-modal-main-elem"))
            .find(".custom-option-wrapper");
        $(".custom-option-wrapper").not($target).hide();
        $target.toggle();
    });

    //modal 창 클릭 시 option 닫음
    $("#modal").click(() => {
        $(".custom-option-wrapper").hide();
    });

    //단계 선택 시 option 닫음
    $(".custom-option").click((e) => {
        const $selected = $(e.target);
        const levelName = $selected.text();
        const level = $selected.data("level");

        $(".custom-option-wrapper").hide();
        $($selected.parents(".custom-modal-main-elem"))
            .find(".group-custom-select")
            .attr("data-level", level)
            .text(levelName);
    })

    //option 위치 조정
    $("#write-level-list")
        .css("top", "171px")
        .css("right", "10%");

    $("#comment-level-list")
        .css("top", "232px")
        .css("right", "10%");

    $("#read-level-list")
        .css("top", "293px")
        .css("right", "10%");
}

//그룹 등급 정보를 custom-option에 반영
function renderGradeOption() {
    for (let item of levelList) {
        const element = $(`<li class="custom-option" data-level="${item.level}">${item.gradeName}</li>`);
        $(".custom-option-wrapper > div").append(element);
    }
}

//모달창 닫기
function closeModal() {
    $("#modal").children().remove();
    $("#modal-dimmed").hide();
}

//카테고리 관리 페이지에 카테고리 반영 및 모달창 닫기
function adaptCategoryHTML(add, index) {
    if (add) appendNewCategoryHTML();
    else editCategoryHTML(index);
    closeModal();
}

//새롭게 추가된 카테고리 반영 (ul 마지막에 추가)
function appendNewCategoryHTML() {
    const type = $(".group-menu-radio:checked").val();
    let html;

    if (type === "BASIC") html = createBasicCategoryHTML();
    else html = createCategoryGroupHTML();

    $("#sortable").append(html);
}

//일반 카테고리 생성
function createBasicCategoryHTML() {
    const item = {
        name: $("#form-category-name").val(),
        description: $("#form-category-description").val(),
        writeLevel: $("#write-level").data("level"),
        commentLevel: $("#comment-level").data("level"),
        readLevel: $("#read-level").data("level"),
        indent: $("#form-category-indent").is(":checked")
    }

    const $element = $(`
        <li class="category-item" data-description="${item.description}" 
            data-category-type="BASIC" 
            data-write-level="${item.writeLevel}" 
            data-comment-level="${item.commentLevel}" 
            data-read-level="${item.readLevel}" 
            data-indent="${item.indent}">
            
            <i class="material-icons-outlined md-18">menu</i>
        </li>
    `);

    const $title = $(`<span class="js-category-title">${item.name}</span>`);
    if (item.indent) {
        const $indent = $(`<div><span>└</span></div>`);
        $indent.append($title);
        $element.prepend($indent);
    } else {
        $element.prepend($title);
    }
    return $element;
}

//카테고리 그룹 생성
function createCategoryGroupHTML() {
    const item = {
        name: $("#form-category-name").val(),
        description: $("#form-category-description").val(),
        fold: $("#form-category-fold").is(":checked")
    }

    const $element = $(`
        <li class="category-group" data-description="${item.description}"
            data-category-type="CATEGORY_GROUP">
            <div>
                <span class="js-category-title">${item.name}</span>
                <div class="fold-wrapper">
                    <span>접기</span>
                    <input type="checkbox">
                </div>
            </div>
            <i class="material-icons-outlined md-18">menu</i>
        </li>
    `);

    $element.find("input[type='checkbox]").prop("checked", item.fold);
    return $element;
}

//카테고리 선택 시 편집 모달 oepn
$(document).on("click", "#sortable > li", (e) => {
    $("#modal-dimmed").show();
    $("#modal").show();
    createEditCategoryModalHTML($(e.target));
})

//카테고리 편집 모달 정보 생성
function createEditCategoryModalHTML($element) {
    const type = $element.data("category-type");
    const item = {
        type: type,
        new: false,
        name: $element.find(".js-category-title").text(),
        description: $element.data("description"),
        index: $element.index()
    }

    createModalHTML(item);

    if (item.type === "BASIC") {
        item.writeLevel = $element.data("write-level");
        item.commentLevel = $element.data("comment-level");
        item.readLevel = $element.data("read-level");
        item.indent = $element.data("indent") === true;
        createBasicCategoryDetailHTML(item);
    } else {
        item.fold = $element.find("input[type='checkbox']").is(":checked");
        createCategoryGroupDetailHTML(item);
    }

    registerFormChanger(item);
}

//편집된 카테고리 반영 (원래 있던 위치에 덮어씀)
function editCategoryHTML(index) {
    const type = $(".group-menu-radio:checked").val();
    let $html;

    if (type === "BASIC") {
        $html = createBasicCategoryHTML();
    } else {
        $html = createCategoryGroupHTML();
    }

    $($("#sortable").children().get(index)).replaceWith($html);
}

$("#modal-dimmed").click(closeModal);

//그룹 제목 접기 버튼을 눌러도 모달창이 열리지 않도록 방지
$(document).on("click", ".category-group input", (e) => {
    e.stopPropagation();
})