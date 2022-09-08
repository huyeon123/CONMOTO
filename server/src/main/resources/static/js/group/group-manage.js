$(function () {
    const width = innerWidth * 0.7;
    $("#invite-modal").dialog({
        title: "그룹 초대",
        Width: width,
        minWidth: 430,
        maxHeight: 320,
        autoOpen: false,
        resizable: false,
        draggable: true,
        modal: true,
        buttons: [
            {
                text: "초대",
                click: function () {
                    inviteMember();
                    $("#invite-modal").dialog("close");
                }
            }
        ]
    });

    $(".ui-dialog-content").css("padding", "0.5em");
    $(".ui-dialog-buttonpane").css("padding", 0);
    $(".ui-dialog-buttonset button").attr("class", "btn-primary");
});

function openInviteModal() {
    $("#invite-modal").dialog("open");
}

async function inviteMember() {
    const url = "/api/group/invite?groupUrl=" + groupUrl;
    const email = $("input[name=invite-email]").val();
    await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: email
    }).then(res => res.json())
        .then(success => {
            if (success) {
                alert("해당 유저에게 초대메세지를 보냈습니다.");
            } else {
                alert("초대 메세지를 보낼 수 없습니다.");
            }
        });
}

function moveToDeletePage() {
    window.location.href = "./delete";
}

function moveToMemberManagePage() {
    window.location.href = "./members";
}

function saveGroupInfo() {
    const url = "/api/group?groupUrl=" + groupUrl;

    const changedName = $('#group-name').val();
    const changedUrl = $('#group-url').val();
    const changedDescription = $('#group-description').val();
    const request = {
        name: changedName,
        url: changedUrl,
        description: changedDescription
    };

    const res = put(url, request);
    res.catch(error => console.error(error));
}