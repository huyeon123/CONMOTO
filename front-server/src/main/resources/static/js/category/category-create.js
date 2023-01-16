async function createCategory() {
    const url = "/api/category";
    const categoryName = $('#target-category-name').val();
    const parentCategoryId = $('#parent-category option:selected').val();

    if (hasInvalidWord(categoryName)) return;

    const request = {
        groupUrl: groupUrl,
        name: categoryName,
        parent: {
            id: parentCategoryId
        }
    };

    post(url, request)
        .then((res) => {
        if (res.ok) {
            location.href = "/workspace/" + groupUrl;
        }
    }).catch(error => console.error(error));
}

function hasInvalidWord(categoryName) {
    const regex = /[ㄱ-ㅎ가-힣\w\s!@#$%^&*()-.,;:'"\[\]{}]+/;
    if (!regex.test(categoryName)) {
        alert("해당 이름은 사용할 수 없습니다.");
        return true;
    }
    return false;
}