document.addEventListener('DOMContentLoaded', () => {
    const dropZoneElement = document.getElementById('drop-zone');
    let fileDisplay = document.getElementById('file-display');
    const submitBtn = document.getElementById('submit-btn');
    let clearBtn = document.getElementById('clear-btn');

    let folders = [];
    let pathsToFilesAndFiles = new Map();

    dropZoneElement.addEventListener("dragover", (e) => {
        e.preventDefault();
        dropZoneElement.classList.add("drop-zone--over");
    });

    dropZoneElement.addEventListener('dragleave', (e) => {
        e.preventDefault();
        dropZoneElement.classList.remove('drop-zone--over');
    });

    ["dragleave", "dragend"].forEach((type) => {
        dropZoneElement.addEventListener(type, () => {
            dropZoneElement.classList.remove("drop-zone--over");
        });
    });

    dropZoneElement.addEventListener("drop", async (e) => {
        e.preventDefault();
        dropZoneElement.classList.remove("drop-zone--over");

        const items = e.dataTransfer.items;

        pathsToFilesAndFiles = await getAllFiles(items);

        if (pathsToFilesAndFiles.size > 0) {
            console.log('Files:', pathsToFilesAndFiles.size);
            let array = [];

            array = new Map([...pathsToFilesAndFiles, ...array]);
            let fold = array.get("folders");

            array.delete("folders");

            displayFiles(array.values(), fold)

        }

    });

    clearBtn.addEventListener('click',  () => {
        console.log(`clearBtn `)
        let data = new Map;
        const f = [];
        data.set("folders", f);
        clearDisplay()
        //submitForm(data);

    });
    submitBtn.addEventListener('click',  () => {
        if (pathsToFilesAndFiles.size > 0) {

            submitForm(pathsToFilesAndFiles);

        }
    });



    function displayFiles(files, folders) {
        fileDisplay.innerHTML = '';

        folders.forEach(folder => {
            const folderItem = document.createElement('div');
            folderItem.classList.add('file-item');
            const folderInfo = document.createElement('div');
            folderInfo.textContent = `${folder} [EMPTY!]`;
            folderItem.appendChild(folderInfo);

            fileDisplay.appendChild(folderItem);
        });

        files.forEach(file => {
            const fileItem = document.createElement('div');
            fileItem.classList.add('file-item');

            if (file.type.startsWith('image/')) {
                const img = document.createElement('img');
                const reader = new FileReader();
                reader.onload = (e) => {
                    img.src = e.target.result.toString();
                };
                reader.readAsDataURL(file);
                fileItem.appendChild(img);
            }

            const fileInfo = document.createElement('div');
            fileInfo.textContent = `${file.name} (${(file.size / 1024).toFixed(2)} KB)`;
            fileItem.appendChild(fileInfo);

            fileDisplay.appendChild(fileItem);
        });
    }

    function clearDisplay() {
        pathsToFilesAndFiles.clear()
        folders = [];
        fileDisplay.innerHTML = '';

    }


    async function getAllFiles(items) {

        // let folders = [];
        // let pathsToFilesAndFiles = new Map();
        pathsToFilesAndFiles.set("folders", folders);
        for (let item of items) {
            const entry = item.webkitGetAsEntry();
            if (entry) {
                pathsToFilesAndFiles = await traverseFileTree(entry, pathsToFilesAndFiles);
            }
        }

        return pathsToFilesAndFiles;
    }

    async function traverseFileTree(item, pathsToFilesAndFiles) {

        if (item.isFile) {
            const file = await new Promise((resolve) => item.file(resolve));

            const pathToFile = item.fullPath

            pathsToFilesAndFiles.set(pathToFile, file);
        } else if (item.isDirectory) {
            const reader = item.createReader();
            const entries = await new Promise((resolve) => reader.readEntries(resolve));
            console.log(`size entries   `, entries.length)

            for (let entry of entries) {
                pathsToFilesAndFiles = await traverseFileTree(entry, pathsToFilesAndFiles/*, `${path}${item.name}/`*/);

            }
            if (entries.length === 0) {

                pathsToFilesAndFiles.get("folders").push(item.fullPath);
            }

        }

        return pathsToFilesAndFiles;
    }

    function submitForm(pathsToFilesAndFiles) {
        const formData = new FormData();

        pathsToFilesAndFiles.forEach((value, key) => {
            formData.append(key, value);
        })

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/dropzone', true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                clearDisplay();
                console.log('Files successfully uploaded');
                location.reload(true);
            } else {
                document.body.innerHTML=xhr.responseText;
                console.error('An error occurred!');
            }

        };
        xhr.send(formData);

    }

});