// SETTINGS
// ============================================
var
  gulp = require('gulp'),
  gconcat = require('gulp-concat'),
  sync = require('browser-sync'),
  sequence = require('run-sequence'),
  rename = require('gulp-rename'),
  webserver = require('gulp-webserver'),
  sass = require('gulp-sass'),
  clean = require('rimraf');

var watch_target = [
  './**/*.html',
  './styles/**/*.css',
  './scripts/**/*.js',
  './sass/*.scss',
  './elements/**/*.html',
];

// TASK
// ============================================
gulp.task('default', function (cb) {
  return sequence(
    'sync',
    'css_concat',
    'watch',
    cb
  );
});

gulp.task('watch', function () {
  gulp.watch(watch_target, function () {
    sequence(
      'css_concat',
      'reload'
    );
  });
});

// sassのコンパイル
// ------------------------------------------
gulp.task('sass', ['clean'], function () {
  gulp.src('./sass/**/*.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest('./styles'));
});

// CSSの結合
// ------------------------------------------
gulp.task('css_concat',['sass'], function() {
  return gulp.src('./styles/*.css')
    .pipe(gconcat('concat.css'))
    .pipe(gulp.dest('./assets/styles'));
});

// サーバーの起動
// ------------------------------------------
gulp.task('webserver', function (cb) {
  gulp.src('./')
    .pipe(webserver({
      port: 8000,
      livereload: true,
      directoryListening: true,
      open: true
    }));
});

// browser sync
// ------------------------------------------
gulp.task('sync', function (cb) {
  sync({
    server: {
      baseDir: './'
    }
  });
  cb();
});

// リロード
// ------------------------------------------
gulp.task('reload', function (cb) {
  sync.reload();
  cb();
});

// お掃除
// ------------------------------------------
gulp.task('clean', function (cb) {
  clean('./assets/*', cb);
});

