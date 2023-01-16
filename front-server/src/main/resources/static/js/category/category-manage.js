$(function () {
    $("input").attr("readonly", true);

    //각 카테고리 별 input 박스 width 조절
    $("input").each((idx, element) => {
        const value = element.value;
        const parent = element.parentElement;
        parent.innerHTML += "<div id='virtual_dom'>" + value + "</div>";

        const calWidth = $('#virtual_dom').width() + 10;
        $('#virtual_dom').remove();

        $('#' + element.id).css("width", calWidth);
    })

    $("#sortable")
        .sortable({
            revert: true,
            items: "div:not(#root)",
            containment: "document",
            cursor: "move",
            placeholder: "ui-state-highlight"
        })
        .disableSelection();
});

//각 카테고리 별 Level Up/Down 버튼 추가
$(function () {
    $('.ui-state-default').not('#root').append(
        `<div class="flex-space-between">
            <button class="btn default change-level">
                <span class="material-icons material-symbols-outlined change-level__up">keyboard_arrow_up</span>
            </button>
            <button class="btn default change-level">
                <span class="material-icons material-symbols-outlined change-level__down">keyboard_arrow_down</span>
            </button>
        </div>`
    )

    $('.level1 .change-level__up').each((idx, icon) => hide(icon.parentElement));
    $('.level3 .change-level__down').each((idx, icon) => hide(icon.parentElement));
});

$(document).on('click', '.change-level__up', (e) => {
    const upButton = e.target.parentElement;
    const target = e.target.parentElement.parentElement.parentElement;
    changeLevelUp(upButton, target);
});

$(document).on('click', '.change-level__down', (e) => {
    const downButton = e.target.parentElement;
    const target = e.target.parentElement.parentElement.parentElement;
    changeLevelDown(downButton, target);
});

function changeLevelUp(upButton, target) {
    const downButton = upButton.nextElementSibling;
    const className = target.className;
    if (className.includes("level2")) {
        target.classList.replace("level2", "level1")
        hide(upButton);
    }
    if (className.includes("level3")) {
        target.classList.replace("level3", "level2")
        display(upButton);
        display(downButton);
    }
}

function changeLevelDown(downButton, target) {
    const upButton = downButton.previousElementSibling;
    const className = target.className;
    if (className.includes("level1")) {
        target.classList.replace("level1", "level2")
        display(upButton);
    }
    if (className.includes("level2")) {
        target.classList.replace("level2", "level3")
        hide(downButton);
    }
}

function hide(button) {
    if (button != null) {
        button.disabled = true;
    }
}

function display(button) {
    button.disabled = false;
}

async function save() {
    const url = "/api/category?url=" + groupUrl;

    const request = getCategoryInfo();

    put(url, request)
        .then(() => window.location.href = "/workspace/" + groupUrl)
        .catch(error => {
            alert("저장에 실패했습니다!");
            console.error(error)
        });
}

function getCategoryInfo() {
    const root = $("input[name='root']").get(0);
    let family = {
        level1: null,
        level2: null,
    };
    let upperCategoryLevel = 0;
    const request = [];
    try {
        $('input').not(root)
            .each((idx, category) => {
                checkValidate(idx, category, upperCategoryLevel);
                extractAndPush(idx, category, family, request);
                upperCategoryLevel = getCategoryLevel(category);
            });
        return request;
    } catch (e) {
        console.error(e);
        alert(e);
    }
}

function checkValidate(idx, category, upperCategoryLevel) {
    const categoryLevel = getCategoryLevel(category);
    if (idx === 0) {
        checkFirstCategoryValidate(categoryLevel);
    } else {
        filterThirdLevel(categoryLevel, upperCategoryLevel);
    }
    return categoryLevel;
}

function checkFirstCategoryValidate(categoryLevel) {
    if (categoryLevel !== 1) {
        throw "최상단 카테고리에는 최상위 카테고리가 존재해야 합니다.";
    }
}

function filterThirdLevel(categoryLevel, upperCategoryLevel) {
    if (categoryLevel === 3) {
        checkThirdLevelCategoryValidate(upperCategoryLevel);
    }
}

function checkThirdLevelCategoryValidate(upperCategoryLevel) {
    if (upperCategoryLevel === 1) {
        throw "Level3 바로 위에 Level1이 존재할 수 없습니다.";
    }
}

function extractAndPush(idx, category, family, request) {
    const name = category.value;
    //parentIdx는 부모 Id를 바로 알 수가 없기때문에(해당 카테고리 위에 랜덤 개수의 카테고리가 있으므로)
    //리스트에서 Idx로 먼저 조회한 후 Id를 가져오는 방식
    const level = getCategoryLevel(category);
    const parentIdx = extractParentIdx(idx, level, family);
    const parent = parentIdx == null ? null : request.at(parentIdx);

    const categoryDto = {
        id: category.id,
        name: name,
        parent: parent
    }

    request.push(categoryDto);
}

function extractParentIdx(categoryIdx, categoryLevel, family) {
    if (categoryLevel === 1) {
        family.level1 = categoryIdx;
        return null;
    } else if (categoryLevel === 2) {
        family.level2 = categoryIdx;
        return family.level1;
    } else if (categoryLevel === 3) {
        return family.level2;
    }
}

function getCategoryLevel(category) {
    const containerClasses = category.parentElement.parentElement.classList;
    if (containerClasses.contains("level1")) {
        return 1;
    } else if (containerClasses.contains("level2")) {
        return 2;
    } else if (containerClasses.contains("level3")) {
        return 3;
    }
}

$(document).on('click', '.material-symbols-outlined.edit', (e) => {
    const target = e.target.previousElementSibling;
    target.readOnly = false;
});

$(document).on('click', '.material-symbols-outlined.delete', (e) => {
    const target = e.target.parentElement.parentElement;
    target.remove();
});

$(document).on('keyup', 'input', (e) => {
    if (e.keyCode === 13) {
        e.target.blur();
        e.target.readOnly = true;
    }
});

$(document).on('keydown', 'input', (e) => {
    const eventLocationId = e.target.id;
    const $eventLocation = $('#' + eventLocationId);
    const $parent = $eventLocation.parent();
    const value = e.target.value;

    const $virtual_dom = $('<div id="virtual_dom">' + value + '</div>');
    $parent.append($virtual_dom);

    const calWidth = $('#virtual_dom').width() + 10;
    const newWidth = calWidth < 850 ? calWidth : 850;
    $('#virtual_dom').remove();

    $eventLocation.css("width", newWidth);
});

function moveToGroupPage() {
    location.href = "/workspace/" + groupUrl;
}