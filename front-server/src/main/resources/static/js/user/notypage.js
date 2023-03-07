let lastIndex = Number.MAX_SAFE_INTEGER;

function pageRender() {
    getNoty()
        .then(res => res.json())
        .then(data => {
            data.forEach((noty, index) => {
                $("#noty-container").append(createNotyHTML(noty));
                setLastIndex(index, data);
            });
        })
        .catch(error => console.error(error));
}

function getNoty() {
    isFetching = true;
    const url = "/api/noty/mine?lastIndex=" + lastIndex;
    return get(url);
}

function setLastIndex(index, data){
    if (index === data.length - 1) {
        lastIndex = data[index].id;
    }
}

function onEdit() {
    clearMenu();
    activateMenu();
    renderCheckbox();
}

function clearMenu() {
    const $inner = $(".noty-menu-inner");
    $inner.children().remove();
}

function activateMenu() {
    const $element = $(`
        <div>
            <button class="noty-menu-button" onclick="offEdit()">완료</button>
            <button class="noty-menu-button" onclick="readWasChecked()">읽음</button>
        </div>
        <button class="noty-menu-button warn" onclick="remove()">삭제</button>
    `);
    
    $(".noty-menu-inner").append($element);
}

function renderCheckbox() {
    const checkbox = $(`
        <div class="noty-menu-check-wrapper">
            <input class="noty-menu-check" type="checkbox">
        </div>
    `);

    $("#noty-container li").prepend(checkbox);
}

function readWasChecked() {
    let request = [];
    $("#noty-container li").forEach((item, index) => {
        if ($(item).find(".noty-menu-check").is(":checked")) {
            const notyId = $(item).attr("id").slice("noty_".length);
            request.push(notyId);
            $(item).addClass("read");
        }
    });

    if (request.length < 1) return;

    requestSetRead(request);
}

function remove() {
    let request = [];
    $("#noty-container li").forEach((item, index) => {
        if ($(item).find(".noty-menu-check").is(":checked")) {
            const notyId = $(item).attr("id").slice("noty_".length);
            request.push(notyId);
            $(item).remove();
        }
    });

    if (request.length < 1) return;

    const url = "/api/noty";
    del(url, request)
        .then(res => {
            if (res.ok) {
                alert("정상 반영되었습니다.");
            }
        })
}

function offEdit() {
    clearMenu();
    inactiveMenu();
    removeCheckbox();
}

function inactiveMenu() {
    const $element = $(`
        <button class="noty-menu-button" onclick="onEdit()">편집</button>
    `);

    $(".noty-menu-inner").append($element);
}

function removeCheckbox() {
    $("#noty-container li")
        .find(".noty-menu-check-wrapper")
        .remove();
}