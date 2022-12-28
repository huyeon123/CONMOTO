async function createCategory() {
    const url = "/api/category?groupUrl=" + groupUrl;
    const categoryName = $('#target-category-name').val();
    const parentCategoryId = $('#parent-category option:selected').val();

    if (hasInvalidWord(categoryName)) return;

    const request = {
        name: categoryName,
        categoryId: parentCategoryId
    };

    const res = post(url, request);
    res.then((data) => {
        if (data) {
            location.href = "/workspace/" + groupUrl;
        }
    }).catch(error => console.error(error));
}

function hasInvalidWord(categoryName) {
    const regex = "[\\w\\s!@#$%^&*()-.,;:\'\"\[\]{}]+";
    if (!regex.test(categoryName)) {
        alert("해당 이름은 사용할 수 없습니다.");
        return true;
    }
    return false;
}