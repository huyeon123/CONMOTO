function inviteMember() {

}

function moveToDeletePage() {
    window.location.href = "./delete";
}

function moveToMemberManagePage() {
    window.location.href = "./members";
}

function saveGroupInfo() {
    const url = baseUrl + "/api/group?groupUrl=" + groupUrl;

    const changedName = $('#group-name').val();
    const changedUrl = $('#group-url').val();
    const changedDescription = $('#group-description').val();
    const request = {
        name: changedName,
        url: changedUrl,
        description: changedDescription
    };

    const res = put(url, request);
    let success = false;
    res.then((data) => {
        if (data) success = data;
    })
    return success;
}