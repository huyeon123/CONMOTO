let page = 0;

function pageRender() {
    getNoty()
        .then(res => res.json())
        .then(data => {
            data.forEach(noty => drawNotyList(noty));
            page++;
        })
        .catch(error => console.error(error));
}

function getNoty() {
    isFetching = true;
    const url = "/api/noty/" + page;
    return get(url);
}

function drawNotyList(data) {
    $(".content-body").append(
        `<div class="noty full-size" id="${data.id}">
                <div class="noty__profile">프로필</div>
                    <div class="noty__content">
                        <div class="noty__header">
                            <div>${data.senderName}</div>
                            <div class="time">${data.sendTime}</div>
                        </div>
                    <pre class="message">${data.message}</pre>
                </div>
            </div>`
    )
}