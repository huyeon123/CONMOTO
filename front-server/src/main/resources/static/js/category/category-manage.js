$(() => {
    $("#sortable")
        .sortable({
            axis: "y",
            containment: "parent",
            handle: "i",
            cursor: "move",
            tolerance: "pointer",
            placeholder: "ui-state-highlight"
        })
        .disableSelection();
})

function pageRender() {
    fetchGrade();
}

//그룹 등급 정보 불러오기
let levelList;
function fetchGrade() {
    const url = "/api/group/grade?url=" + groupUrl;
    get(url)
        .then(res => res.json())
        .then(data => levelList = data.levelList);
}

//카테고리 정보 전체 저장
function saveCategory() {
    const url = "/api/category/save?url=" + groupUrl;
    const request = [];

    const $categoryItem = $("#sortable").children();

    $categoryItem.each((index, item) => {
        const $item = $(item);
        const requestElem = {
            groupUrl: groupUrl,
            name: $($item.find(".js-category-title")).text(),
            description: $item.attr("data-description"),
            type: $item.attr("data-category-type")
        }

        if (requestElem.type === "CATEGORY_GROUP") {
            requestElem.fold = $($item.find("input:checkbox")).is(":checked");
        } else {
            requestElem.indent = $item.attr("data-indent") === "true";
            requestElem.availableWriteLevel = $item.attr("data-write-level");
            requestElem.availableCommentLevel = $item.attr("data-comment-level");
            requestElem.availableReadLevel = $item.attr("data-read-level");
        }

        request.push(requestElem);
    })

    post(url, request)
        .then((res) => {
            if (res.ok) {
                alert("정상 반영되었습니다.");
                location.href = "/community/" + groupUrl;
            }
        })
        .catch(error => {
            alert("저장에 실패했습니다!");
            console.error(error)
        });
}

}

    }
}

}

}

}


    }

}

    }
}

    }
}

$(document).on('click', '.material-symbols-outlined.edit', (e) => {
    const target = e.target.previousElementSibling;
    target.readOnly = false;
});


    }





