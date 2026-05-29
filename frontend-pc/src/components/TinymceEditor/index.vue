<!-- 富文本编辑器 -->
<template>
  <component v-if="inlineEditor" :is="tagName" :id="elementId" />
  <textarea v-else :id="elementId"></textarea>
</template>

<script setup>
  import {
    watch,
    onMounted,
    onBeforeUnmount,
    onActivated,
    onDeactivated,
    nextTick,
    useAttrs
  } from 'vue';
  import tinymce from 'tinymce/tinymce';
  import 'tinymce/themes/silver';
  import 'tinymce/icons/default';
  import 'tinymce/plugins/code';
  import 'tinymce/plugins/preview';
  import 'tinymce/plugins/fullscreen';
  import 'tinymce/plugins/paste';
  import 'tinymce/plugins/searchreplace';
  //import 'tinymce/plugins/save';
  //import 'tinymce/plugins/autosave';
  import 'tinymce/plugins/link';
  import 'tinymce/plugins/autolink';
  import 'tinymce/plugins/image';
  import 'tinymce/plugins/media';
  import 'tinymce/plugins/table';
  import 'tinymce/plugins/codesample';
  import 'tinymce/plugins/lists';
  import 'tinymce/plugins/advlist';
  import 'tinymce/plugins/hr';
  import 'tinymce/plugins/charmap';
  import 'tinymce/plugins/emoticons';
  import 'tinymce/plugins/anchor';
  import 'tinymce/plugins/directionality';
  import 'tinymce/plugins/pagebreak';
  import 'tinymce/plugins/quickbars';
  import 'tinymce/plugins/nonbreaking';
  import 'tinymce/plugins/visualblocks';
  import 'tinymce/plugins/visualchars';
  import 'tinymce/plugins/wordcount';
  import 'tinymce/plugins/emoticons/js/emojis';
  import { storeToRefs } from 'pinia';
  import { useThemeStore } from '@/store/modules/theme';
  import { uploadFile } from '@/api/system/file';
  import {
    DEFAULT_CONFIG,
    DARK_CONFIG,
    uuid,
    bindHandlers,
    openAlert
  } from './util';

  defineOptions({ name: 'TinymceEditor' });

  const props = defineProps({
    /** 编辑器唯一id */
    id: String,
    /** v-model */
    modelValue: String,
    /** 编辑器配置 */
    init: Object,
    /** 是否内联模式 */
    inline: Boolean,
    /** model events */
    modelEvents: {
      type: String,
      default: 'change input undo redo'
    },
    /** 内联模式标签名 */
    tagName: {
      type: String,
      default: 'div'
    },
    /** 是否禁用 */
    disabled: Boolean,
    /** 是否跟随框架主题 */
    autoTheme: {
      type: Boolean,
      default: true
    },
    /** 不跟随框架主题时是否使用暗黑主题 */
    darkTheme: Boolean
  });

  const emit = defineEmits(['update:modelValue']);

  const attrs = useAttrs();
  const themeStore = useThemeStore();
  const { darkMode } = storeToRefs(themeStore);

  /** 编辑器唯一id */
  const elementId = props.id || uuid('tiny-vue');

  /** 编辑器实例 */
  let editorIns = null;

  /** 是否内联模式 */
  const inlineEditor = props.init?.inline || props.inline;

  /** 更新value */
  const updateValue = (value) => {
    emit('update:modelValue', value);
  };

  /** 修改内容 */
  const setContent = (value) => {
    if (
      editorIns &&
      typeof value === 'string' &&
      value !== editorIns.getContent()
    ) {
      editorIns.setContent(value);
    }
  };

  /** 渲染编辑器 */
  const render = () => {
    const isDark = props.autoTheme ? darkMode.value : props.darkTheme;
    tinymce.init({
      ...DEFAULT_CONFIG,
      ...(isDark ? DARK_CONFIG : {}),
      images_upload_handler: (blobInfo, success, error) => {
        const file = blobInfo.blob();
        uploadFile(file, void 0, file.name)
          .then((res) => {
            success(res.url);
          })
          .catch((e) => {
            console.error(e);
            error(e.message);
          });
      },
      file_picker_callback: (callback, value, meta) => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        if (meta.filetype === 'image') {
          input.setAttribute('accept', 'image/*');
        } else if (meta.filetype === 'media') {
          input.setAttribute('accept', 'video/*, audio/*');
        }
        input.onchange = () => {
          const file = input.files?.[0];
          if (!file) {
            return;
          }
          uploadFile(file, void 0, file.name)
            .then((res) => {
              callback(res.url, { text: file.name, alt: file.name });
            })
            .catch((e) => {
              console.error(e);
              alert({ content: e.message });
            });
        };
        input.click();
      },
      ...props.init,
      selector: `#${elementId}`,
      readonly: props.disabled,
      inline: inlineEditor,
      setup: (editor) => {
        editorIns = editor;
        editor.on('init', (e) => {
          // 回显初始值
          if (props.modelValue) {
            setContent(props.modelValue);
          }
          // v-model
          editor.on(props.modelEvents, () => {
            updateValue(editor.getContent());
          });
          // valid events
          bindHandlers(e, attrs, editor);
        });
        if (typeof props.init?.setup === 'function') {
          props.init.setup(editor);
        }
      }
    });
  };

  /** 销毁编辑器 */
  const destory = () => {
    if (tinymce != null && editorIns != null) {
      tinymce.remove(editorIns);
      editorIns = null;
    }
  };

  /** 弹出提示框 */
  const alert = (option) => {
    openAlert(editorIns, option);
  };

  defineExpose({ editorIns, alert });

  watch(
    () => props.modelValue,
    (val, prevVal) => {
      if (val !== prevVal) {
        setContent(val || '');
      }
    }
  );

  watch(
    () => props.disabled,
    (disable) => {
      if (editorIns !== null) {
        if (typeof editorIns.mode?.set === 'function') {
          editorIns.mode.set(disable ? 'readonly' : 'design');
        } else {
          editorIns.setMode(disable ? 'readonly' : 'design');
        }
      }
    }
  );

  watch(
    () => props.tagName,
    () => {
      destory();
      nextTick(() => {
        render();
      });
    }
  );

  watch(darkMode, () => {
    if (props.autoTheme) {
      destory();
      nextTick(() => {
        render();
      });
    }
  });

  onMounted(() => {
    render();
  });

  onBeforeUnmount(() => {
    destory();
  });

  onActivated(() => {
    render();
  });

  onDeactivated(() => {
    destory();
  });
</script>

<style lang="scss">
  body {
    .tox.tox-tinymce-aux,
    &.tox-fullscreen .tox.tox-tinymce-aux {
      z-index: 19990000;
    }

    .tox-menu.tox-collection.tox-collection--list {
      max-height: 420px !important;
    }

    textarea[id^='tiny-vue'] {
      width: 0;
      height: 0;
      margin: 0;
      padding: 0;
      opacity: 0;
      box-sizing: border-box;
    }

    .tox .tox-menubar,
    .tox .tox-toolbar-overlord,
    .tox .tox-edit-area__iframe,
    .tox .tox-statusbar {
      background: none;
    }

    .tox .tox-toolbar,
    .tox .tox-toolbar__overflow,
    .tox .tox-toolbar__primary {
      background: none;
      box-shadow: 0 -0.8px 0 var(--el-border-color-light) inset;
    }
  }
</style>
