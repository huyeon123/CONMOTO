async function createGroup(url = "/api/group") {
    const groupName = $('#js-group-name').val();
    const description = $('#js-group-description').val();
    const urlPath = $('#js-group-url').val();
    const request = {
        name: groupName,
        url: urlPath,
        description: description
    };

    post(url, request)
        .then(res => {
            if (res.ok) {
                res.text().then((url) => {
                    alert("그룹이 생성되었습니다.");
                    location.href = "/workspace/" + url;
                })
            }
        })
        .catch(error => console.error(error));
}