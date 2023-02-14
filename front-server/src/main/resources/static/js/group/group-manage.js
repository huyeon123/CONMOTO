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
    $(".ui-dialog-buttonset button").attr("class", "default-style-btn default");
});

function openInviteModal() {
    $("#invite-modal").dialog("open");
}

function inviteMember() {
    const url = "/api/group/" + groupUrl + "/invite";
    const request = {email: $("input[name=invite-email]").val()};
    post(url, request)
        .then(res => {
            if (res.ok) {
                alert("해당 유저에게 초대메세지를 보냈습니다.");
            } else {
                alert("초대에 실패했습니다.");
            }
        })
}

function moveToDeletePage() {
    window.location.href = "./manage/delete";
}

function moveToMemberManagePage() {
    window.location.href = "./manage/members";
}

function moveToCategoryManagePage() {
    window.location.href = "./manage/category";
}

function saveGroupInfo() {
    const url = "/api/group/" + groupUrl;

    const changedName = $('#group-info-name').val();
    const changedDescription = $('#group-description').val();
    const request = {
        name: changedName,
        description: changedDescription
    };

    put(url, request)
        .then(res => {
            if (res.ok) {
                alert("정상적으로 반영되었습니다.");
            } else alert("반영에 실패했습니다.");
        }).catch(error => console.error(error));
}