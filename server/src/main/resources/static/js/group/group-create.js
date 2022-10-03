async function createGroup(url = "/api/group") {
    const groupName = $('#new-group-name').val();
    const description = $('#group-description').val();
    const urlPath = $('#url-path').val();
    const request = {
        name: groupName,
        description: description,
        url: urlPath
    };

    post(url, request)
        .then(res => {
            if (canGetData(res)) return getData(res);
        })
        .then((data) => {
            alert("그룹이 생성되었습니다.");
            location.href = "/workspace/" + data.url;
        })
        .catch(error => console.error(error));
}