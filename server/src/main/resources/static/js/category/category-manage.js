$(function () {
    $("input").attr("readonly", true);

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
    const url = "/api/category?groupUrl=" + groupUrl;

    const request = getCategoryInfo();

    const res = await put(url, request);
    res.then(success => {
        if (success) {
            alert("정상 반영되었습니다.");
            window.location.href = "/workspace/" + groupUrl;
        }
    }).catch(error => console.error(error));
}

function getCategoryInfo() {
    const root = $("input[name='root']").get(0);
    let family = {
        root: 0,
        level1: null,
        level2: null,
        level3: null,
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
    //root를 제외했기 때문에 +1을 해줌
    const parentIdx = extractParentLevel(idx + 1, category, family);

    const categoryDto = {
        name: name,
        parentId: parentIdx,
    }

    request.push(categoryDto);
}

function extractParentLevel(categoryIdx, category, family) {
    const categoryLevel = getCategoryLevel(category);
    if (categoryLevel === 1) {
        family.level1 = categoryIdx;
        return family.root;
    } else if (categoryLevel === 2) {
        family.level2 = categoryIdx;
        return family.level1;
    } else if (categoryLevel === 3) {
        return family.level2;
    }
}

function getCategoryLevel(category) {
    const containerClasses = category.parentElement.classList;
    if (containerClasses.contains("level1")) {
        return 1;
    } else if (containerClasses.contains("level2")) {
        return 2;
    } else if (containerClasses.contains("level3")) {
        return 3;
    }
}

$(document).on('click', '.edit', (e) => {
    const target = e.target.previousElementSibling;
    target.readOnly = false;
});

$(document).on('keyup', 'input', (e) => {
    if (e.keyCode === 13) {
        e.target.blur();
        e.target.readOnly = true;
    }
});