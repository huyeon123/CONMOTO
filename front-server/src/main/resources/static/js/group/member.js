//관리자 - 등급 관리 용
let levelList;

function fetchGrade() {
    const url = "/api/group/grade?url=" + groupUrl;
    get(url)
        .then(res => res.json())
        .then(data => levelList = data.levelList);
}
