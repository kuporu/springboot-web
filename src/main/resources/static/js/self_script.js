// 类方法
class Slider {
  constructor (rangeElement, valueElement, options, scoreElement, allInputElement) {
    this.rangeElement = rangeElement
    this.valueElement = valueElement
    this.options = options
    this.scoreElement = scoreElement
    this.allInputElement = allInputElement

    // Attach a listener to "change" event
    // 这里的this绑定Slider?
    // 修改事件，参考 https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/change_event
    this.rangeElement.addEventListener('mouseup', this.upload.bind(this))
    this.rangeElement.addEventListener('input', this.updateSlider.bind(this))
  }

  // Initialize the slider
  init() {
    this.rangeElement.setAttribute('min', options.min)
    this.rangeElement.setAttribute('max', options.max)
    this.rangeElement.value = options.cur

    // this.updateSlider()
  }

  // 分数样式
  asMoney(value) {
    return parseFloat(value) + '分'
      .toLocaleString('en-US', { maximumFractionDigits: 2 })
  }

  updateSlider () {
    this.valueElement.innerHTML = this.asMoney(this.rangeElement.value)
  }

  upload () {
    this.scoreElement.innerHTML = "已评分"
    this.allInputElement.style.display = "none"
    var d = {};
    d.score = this.rangeElement.value;
    $.ajax({
      url: "addScore",
      data: JSON.stringify(d),
      //type、contentType必填,指明传参方式
      type: "POST",
      contentType: "application/json;charset=utf-8",
      success: function(response){
        //前端调用成功后，可以处理后端传回的json格式数据。
        if(response.success){
          alert(response.message);
        }
      }
    });
  }
}

let rangeElement = document.querySelector('.range [type="range"]')
let allInputElement = document.getElementById("allInput")
let valueElement = document.getElementById("span3")
let scoreElement = document.getElementById("span2")

let options = {
  min: 0,
  max: 100,
  cur: 0
}

if (rangeElement) {
  let slider = new Slider(rangeElement, valueElement, options, scoreElement, allInputElement)

  slider.init()
}

// 常规方法
// rangeElement.addEventListener('change', upload)

/**
 * 滑块变化后函数
 * 向分数模块中（scoreElement）添加“已评分”，隐藏滑块、标签模块（allInputElement）
 * 通过Ajax将分数上传到后端
 */
// function upload () {
//   scoreElement.innerHTML = "已评分了吧"
//   allInputElement.style.display = "none"
//   var d = {};
//   d.score = this.rangeElement.value;
//   $.ajax({
//     url: "addScore",
//     data: JSON.stringify(d),
//     //type、contentType必填,指明传参方式
//     type: "POST",
//     contentType: "application/json;charset=utf-8",
//     success: function(response){
//       //前端调用成功后，可以处理后端传回的json格式数据。
//       if(response.success){
//         alert(response.message);
//       }
//     }
//   });
// }

