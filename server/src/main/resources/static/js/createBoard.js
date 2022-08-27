async function createBoard(url = "/api/board") {
    await fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
        body: groupUrl,
    })
        .then((response) => response.json())
        .then((data) => {
            alert(data.message);
            if (data.success) {
                window.location.href = "/workspace/" + groupUrl + "/board/" + data.data;
            }
        })
        .catch((error) => console.log("실패 : ", error));
}