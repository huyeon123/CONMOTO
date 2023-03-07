//검색창 입력시 필터링 이벤트 감지
$("#member-search").keyup(showSearchResults);

//데이터 필터링
function showSearchResults() {
    const searchText = $("#member-search").val();
    const seeAll = (searchText === "");

    const regex = new RegExp(searchText, "gi");

    //멤버들 중 닉네임 혹은 이메일이 검색어와 일치하면 해당 멤버 엘리먼트만 display
    $("ul.scrollable .member-info").each((idx, element) => {
        const $element = $(element);
        if (seeAll) {
            $element.show();
        } else {
            const itemText = $element.find(".js-member-nickname-email").text();
            if (itemText.match(regex)) $element.show();
            else $element.hide();
        }
    });
}

function moveToMemberPage(elementId) {
    const id = elementId.slice("member_".length);
    location.href = "./members/" + id;
}