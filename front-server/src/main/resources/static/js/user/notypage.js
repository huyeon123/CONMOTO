let page = 0;

$(window).on('load', () => {
    const url = "/api/noty/" + page;
    get(url)
        .then(res => {
            if (canGetData(res)) {
                res.json().then(data => {
                    data.forEach(noty => drawNotyList(noty));
                    page++;
                })

            }
        });
});

function drawNotyList(data) {
    $(".content__body").append(
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