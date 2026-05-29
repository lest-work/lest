import type { App } from 'vue';
import ElementPlus from 'element-plus';
import EleAdminPlus from 'ele-admin-plus';
import 'element-plus/theme-chalk/src/index.scss';
import 'ele-admin-plus/es/style/index.scss';
import 'cropperjs/dist/cropper.css';
import 'xgplayer/dist/index.min.css';

/** 全局安装(开发环境) */
const installer = {
  install(app: App) {
    app.use(ElementPlus);
    app.use(EleAdminPlus);
  }
};

export default installer;
