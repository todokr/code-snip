var CS = window.CS || {};

CS.JsonSubmitManager = function (form) {
  this.loginForm = form;
  this.btn = form;

  this.bindEvent();
};

CS.JsonSubmitManager.prototype = {
  bindEvent: function() {
    var _self = this;
    // this.$btn.on('click', function(e) {
    //   console.log("click!");
    //   _self.log();
    // });
  },
  log: function(){
    console.log(this);
  }
};
