let ckEditor5;

DecoupledDocumentEditor
    .create(document.querySelector('#viewer'))
    .then(editor => {
        ckEditor5 = editor;
        ckEditor5.setData(html);
        ckEditor5.enableReadOnlyMode("#viewer");
    })
    .catch(error => {
        console.error(error);
    });