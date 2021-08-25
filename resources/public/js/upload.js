const $upload = document.querySelector('#upload')
$upload.addEventListener('change', _ => {
  upload($upload.files[0])
})

function upload(file) {
  const formData = new FormData()
  formData.append('file', file)

  const folder = location.pathname === '/' ? "/folder" : location.pathname

  fetch(`${folder}/${file.name}`, {
    method: 'POST',
    body: formData
  }).then(_ => {
    location = location
  }).catch(error => console.log(error))
}
