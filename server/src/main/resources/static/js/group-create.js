async function createGroup(url = "/api/group") {
    const groupName = $('#group-name').val();
    const description = $('#group-description').val();
    const urlPath = $('#url-path').val();
    const request = {
        name: groupName,
        description: description,
        url: urlPath
    };

    post(url, request)
        .then((data) => {
            alert(data.message);
            if (data.success) {
                location.href = "/workspace/" + data.data.url;
            }
        })
        .catch(error => console.error(error));
}