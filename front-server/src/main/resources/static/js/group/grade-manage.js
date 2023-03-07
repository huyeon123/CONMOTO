function pageRender() {
    loadCurrentGradeInfo();
}

function openOptions() {
    $(".custom-option-wrapper").toggle();
}

function loadCurrentGradeInfo() {
    const url = "/api/group/grade?url=" + groupUrl;
    get(url)
        .then(res => res.json())
        .then(data => {
            const levelList = data.levelList;
            const currentLevel = levelList.length;
            $("#currentLevel").text(currentLevel + "단계");

            levelList.forEach((item, index) => {
                const $element = createGradeItemHtml(index + 1);
                $element.find("#level-name").val(item.gradeName);
                $element.find("#level-description").val(item.gradeDescription);
                $element.find("#condition-post-count").val(item.postCondition);
                $element.find("#condition-comment-count").val(item.commentCondition);
                $element.find("#condition-duration").val(item.durationCondition);
                $(".scrollable").append($element);
            })
        })
}

//아무데나 클릭 시 option 닫음
$(document).mouseup((e) => {
    const option = $(".custom-option-wrapper");
    if (option.has(e.target).length === 0) {
        option.hide();
    }
});

//단계 선택 시 단계에 맞게 form 생성 및 옵션 창 닫음
$(".custom-option").click((e) => {
    const $selected = $(e.target);
    const level = parseInt($selected.text().replace("단계", ""));
    const currentLevel = parseInt($("#currentLevel").text());

    //옵션 창 닫음
    $("#currentLevel").text(level + "단계");
    $(".custom-option-wrapper").hide();

    //form 생성
    if (currentLevel < level) {
        for (let start = currentLevel + 1; start <= level; start++) {
            const gradeItemHtml = createGradeItemHtml(start);
            $(".scrollable").append(gradeItemHtml);
        }
    } else {
        for (let start = currentLevel; start > level; start--) {
            $(".scrollable li").get(start - 1).remove();
        }
    }

})

//form element
function createGradeItemHtml(level) {
    return $(`
        <li class="grade-item-wrapper">
                    <div class="grade-num-wrapper">
                        <span>${level}</span>
                    </div>
                    <div class="grade-detail">
                        <div class="mb-2">
                            <span>등급 명</span>
                            <input id="level-name" type="text" class="form-control" placeholder="등급명을 입력하세요.">
                        </div>
                        <div class="mb-2">
                            <span>설명</span>
                            <input id="level-description" type="text" class="form-control" placeholder="등급 설명을 입력하세요.">
                        </div>
                        <div>
                            <span>조건</span>
                            <div class="grade-up-condition-wrapper">
                                <div class="grade-up-condition-item bb">
                                    <span>게시글</span>
                                    <div>
                                        <input id="condition-post-count" class="grade-up-condition" type="number" placeholder="0" min="0" max="100000">
                                        <span>회</span>
                                    </div>
                                </div>
                                <div class="grade-up-condition-item bb">
                                    <span>댓글</span>
                                    <div>
                                        <input id="condition-comment-count" class="grade-up-condition" type="number" placeholder="0" min="0" max="1000000">
                                        <span>회</span>
                                    </div>
                                </div>
                                <div class="grade-up-condition-item">
                                    <span>가입 기간</span>
                                    <div>
                                        <input id="condition-duration" class="grade-up-condition" type="number" placeholder="0" min="0" max="10000">
                                        <span>일</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
    `);
}

function saveGradeInfo() {
    const url = "/api/group/grade";
    const request = {
        groupUrl: groupUrl,
        levelList: []
    };

    $(".grade-item-wrapper").each((index, level) => {
        const $level = $(level);
        const item = {
            level: index,
            gradeName: $level.find("#level-name").val(),
            gradeDescription: $level.find("#level-description").val(),
            postCondition: $level.find("#condition-post-count").val(),
            commentCondition: $level.find("#condition-comment-count").val(),
            durationCondition: $level.find("#condition-duration").val()
        };

        request.levelList.push(item);
    });

    post(url, request)
        .then(res => {
            if (res.ok) {
                alert("정상 반영되었습니다.");
            }
        });
}