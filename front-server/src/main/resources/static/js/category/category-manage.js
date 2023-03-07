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





