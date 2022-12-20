async function createCategory() {
    const url = "/api/category?groupUrl=" + groupUrl;
    const categoryName = $('#target-category-name').val();
    const parentCategoryId = $('#parent-category option:selected').val();
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