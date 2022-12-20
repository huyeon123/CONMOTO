async function createGroup(url = "/api/group") {
    const groupName = $('#new-group-name').val();
    const description = $('#group-description').val();
    const urlPath = $('#url-path').val();
    const request = {
        name: groupName,
        url: urlPath,
        description: description
    };

    post(url, request)
        .then(res => {
            if (canGetData(res)) {
                getJson(res).then((data) => {
                    alert("그룹이 생성되었습니다.");
                    location.href = "/workspace/" + data.url;
                })
            }
        })
        .catch(error => console.error(error));
}