<!-- 主题设置抽屉 -->
<template>
  <ele-drawer
    :size="268"
    v-model="visible"
    :title="t('layout.setting.title')"
    :z-index="2002"
    :body-style="{
      padding: 0,
      display: 'flex',
      flexDirection: 'column',
      height: '100%'
    }"
    :append-to-body="true"
    :destroy-on-close="true"
    style="max-width: 100%"
    @open="handleOpen"
  >
    <div class="setting-segmented-wrapper">
      <ele-segmented
        :block="true"
        v-model="tabActive"
        :items="[
          { label: t('layout.setting.tabs.theme'), value: 'theme' },
          { label: t('layout.setting.tabs.layout'), value: 'layout' },
          { label: t('layout.setting.tabs.skin'), value: 'skin' }
        ]"
      />
    </div>
    <div v-if="tabActive === 'theme'" class="setting-wrapper">
      <!-- 侧栏风格 -->
      <div class="setting-theme">
        <ele-tooltip
          :content="t('layout.setting.sideStyles.dark')"
          placement="bottom"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-side-dark"
            @click="updateValue('sidebarStyle', 'dark')"
          >
            <ele-text
              v-if="sidebarStyle === 'dark'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
        <ele-tooltip
          :content="t('layout.setting.sideStyles.light')"
          placement="bottom"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-side-light"
            @click="updateValue('sidebarStyle', 'light')"
          >
            <ele-text
              v-if="sidebarStyle === 'light'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
      </div>
      <!-- 顶栏风格 -->
      <div class="setting-theme">
        <ele-tooltip
          :content="t('layout.setting.headStyles.light')"
          placement="bottom"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-head-light"
            @click="updateValue('headerStyle', 'light')"
          >
            <ele-text
              v-if="headerStyle === 'light'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
        <ele-tooltip
          :content="t('layout.setting.headStyles.dark')"
          placement="bottom"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-head-dark"
            @click="updateValue('headerStyle', 'dark')"
          >
            <ele-text
              v-if="headerStyle === 'dark'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
        <ele-tooltip
          :content="t('layout.setting.headStyles.primary')"
          placement="bottom"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-head-primary"
            @click="updateValue('headerStyle', 'primary')"
          >
            <ele-text
              v-if="headerStyle === 'primary'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
      </div>
      <!-- 主题色 -->
      <div class="setting-colors">
        <div
          v-for="item in themes"
          :key="item.name"
          :style="{ 'background-color': item.color || item.value }"
          class="setting-color-item"
          @click="updateColor(item.value)"
        >
          <el-icon v-if="item.value ? item.value === color : !color">
            <CheckOutlined />
          </el-icon>
          <ele-tooltip
            :content="t('layout.setting.colors.' + item.name)"
            placement="bottom"
            :hide-after="0"
            :offset="9"
          >
            <div class="setting-color-tooltip"></div>
          </ele-tooltip>
        </div>
        <!-- 颜色选择器 -->
        <el-color-picker
          v-model="colorPickerValue"
          :predefine="predefineColors"
          class="setting-color-picker"
          @change="updateColor"
        />
      </div>
      <!-- 暗黑模式 -->
      <div class="setting-item">
        <div class="setting-item-title">{{ t('layout.setting.darkMode') }}</div>
        <div class="setting-item-control" ref="darkSwitchRef">
          <el-switch
            size="small"
            :model-value="darkMode"
            @update:modelValue="updateDarkMode"
          />
        </div>
      </div>
      <!-- 圆角主题 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.roundedTheme') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="roundedTheme"
            @update:modelValue="(val) => updateValue('roundedTheme', val)"
          />
        </div>
      </div>
      <el-divider />
      <!-- 导航布局 -->
      <ele-text type="placeholder" class="setting-title">
        {{ t('layout.setting.layout') }}
      </ele-text>
      <div class="setting-theme hidden-xs-only">
        <ele-tooltip
          :content="t('layout.setting.layoutStyles.side')"
          placement="top"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-side-dark setting-layout-side"
            @click="updateValue('layout', 'default')"
          >
            <ele-text
              v-if="layout === 'default'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
        <ele-tooltip
          :content="t('layout.setting.layoutStyles.top')"
          placement="top"
          :hide-after="0"
          :offset="9"
        >
          <div
            class="setting-head-dark setting-layout-top"
            @click="updateValue('layout', 'top')"
          >
            <ele-text
              v-if="layout === 'top'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
        <ele-tooltip
          :content="t('layout.setting.layoutStyles.mix')"
          placement="top"
          :hide-after="0"
          :offset="9"
        >
          <div class="setting-layout-mix" @click="updateValue('layout', 'mix')">
            <ele-text
              v-if="layout === 'mix'"
              type="primary"
              :icon="CheckOutlined"
            />
          </div>
        </ele-tooltip>
      </div>
      <!-- 侧栏菜单布局 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.sidebarLayout') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="layout === 'top'"
            :model-value="sidebarLayout === 'mix'"
            @update:modelValue="
              (value) => updateValue('sidebarLayout', value ? 'mix' : 'default')
            "
          />
        </div>
      </div>
      <!-- 双侧栏二级风格 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.mixSidebarStyle') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="sidebarLayout !== 'mix'"
            :model-value="mixSidebarStyle === 'dark'"
            @update:modelValue="
              (value) =>
                updateValue('mixSidebarStyle', value ? 'dark' : 'light')
            "
          />
        </div>
      </div>
      <!-- 混合菜单分割 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.menuItemTrigger') }}
        </div>
        <div class="setting-item-control" style="width: 72px">
          <el-select
            size="small"
            :disabled="
              layout === 'top' || (sidebarLayout !== 'mix' && layout !== 'mix')
            "
            :model-value="menuItemTrigger"
            @update:modelValue="(val) => updateValue('menuItemTrigger', val)"
            :popper-options="{ strategy: 'fixed' }"
          >
            <el-option value="route" label="Route" />
            <el-option value="click" label="Click" />
            <el-option value="hover" label="Hover" />
          </el-select>
        </div>
      </div>
      <el-divider />
      <ele-text type="placeholder" class="setting-title">
        {{ t('layout.setting.more') }}
      </ele-text>
      <!-- 固定主体 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.fixedBody') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="fixedBody"
            @update:modelValue="(val) => updateValue('fixedBody', val)"
          />
        </div>
      </div>
      <!-- 固定顶栏 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.fixedHeader') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="fixedBody"
            :model-value="fixedHeader"
            @update:modelValue="(val) => updateValue('fixedHeader', val)"
          />
        </div>
      </div>
      <!-- 固定侧栏 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.fixedSidebar') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="fixedSidebar"
            :disabled="fixedBody || layout === 'top'"
            @update:modelValue="(val) => updateValue('fixedSidebar', val)"
          />
        </div>
      </div>
      <!-- 图标是否置于顶栏 -->
      <div class="setting-item hidden-xs-only">
        <div class="setting-item-title">
          {{ t('layout.setting.logoInHeader') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="logoInHeader"
            :disabled="layout === 'top'"
            @update:modelValue="(val) => updateValue('logoInHeader', val)"
          />
        </div>
      </div>
      <!-- 侧栏彩色图标 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.colorfulIcon') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="colorfulIcon"
            :disabled="layout === 'top'"
            @update:modelValue="(val) => updateValue('colorfulIcon', val)"
          />
        </div>
      </div>
      <!-- 侧栏排它展开 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.uniqueOpened') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="uniqueOpened"
            @update:modelValue="(val) => updateValue('uniqueOpened', val)"
          />
        </div>
      </div>
      <!-- 内容区域铺满 -->
      <div class="setting-item hidden-xs-only">
        <div class="setting-item-title">
          {{ t('layout.setting.fluid') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="fluid"
            @update:modelValue="(val) => updateValue('fluid', val)"
          />
        </div>
      </div>
      <el-divider />
      <ele-text type="placeholder" class="setting-title">
        {{ t('layout.setting.tab') }}
      </ele-text>
      <!-- 页签 -->
      <div class="setting-item">
        <div class="setting-item-title">{{ t('layout.setting.showTabs') }}</div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="tabBar"
            @update:modelValue="(value) => updateValue('tabBar', value)"
          />
        </div>
      </div>
      <!-- 固定主页页签 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.fixedHome') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="!tabBar"
            :model-value="fixedHome"
            @update:modelValue="(val) => updateValue('fixedHome', val)"
          />
        </div>
      </div>
      <!-- 页签置于顶栏 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.tabInHeader') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="!tabBar"
            :model-value="tabInHeader"
            @update:modelValue="(val) => updateValue('tabInHeader', val)"
          />
        </div>
      </div>
      <!-- 页签显示图标 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.tabIcon') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="!tabBar"
            :model-value="tabIcon"
            @update:modelValue="(val) => updateValue('tabIcon', val)"
          />
        </div>
      </div>
      <!-- 刷新保留页签 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.tabsCache') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :disabled="!tabBar"
            :model-value="tabsCache"
            @update:modelValue="(val) => updateValue('tabsCache', val)"
          />
        </div>
      </div>
      <!-- 页签风格 -->
      <div class="setting-item">
        <div class="setting-item-title">{{ t('layout.setting.tabStyle') }}</div>
        <div class="setting-item-control" style="width: 90px">
          <el-select
            size="small"
            :disabled="!tabBar"
            :model-value="tabStyle"
            @update:modelValue="(val) => updateValue('tabStyle', val)"
            :popper-options="{ strategy: 'fixed' }"
          >
            <el-option
              value="simple"
              :label="t('layout.setting.tabStyles.default')"
            />
            <el-option
              value="indicator"
              :label="t('layout.setting.tabStyles.dot')"
            />
            <el-option value="tag" :label="t('layout.setting.tabStyles.tag')" />
            <el-option
              value="button"
              :label="t('layout.setting.tabStyles.card')"
            />
          </el-select>
        </div>
      </div>
      <el-divider />
      <ele-text type="placeholder" class="setting-title">
        {{ t('layout.setting.other') }}
      </ele-text>
      <!-- 切换路由是否缓存 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.pageKeepAlive') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="pageKeepAlive"
            @update:modelValue="(val) => updateValue('pageKeepAlive', val)"
          />
        </div>
      </div>
      <!-- 全局页脚 -->
      <div class="setting-item">
        <div class="setting-item-title">{{ t('layout.setting.footer') }}</div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="footer"
            @update:modelValue="(val) => updateValue('footer', val)"
          />
        </div>
      </div>
      <!-- 色弱模式 -->
      <div class="setting-item">
        <div class="setting-item-title">{{ t('layout.setting.weakMode') }}</div>
        <div class="setting-item-control" ref="weakSwitchRef">
          <el-switch
            size="small"
            :model-value="weakMode"
            @update:modelValue="updateWeakMode"
          />
        </div>
      </div>
      <!-- 响应式开关 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.responsive') }}
        </div>
        <div class="setting-item-control">
          <el-switch
            size="small"
            :model-value="responsive"
            @update:modelValue="updateResponsive"
          />
        </div>
      </div>
      <!-- 切换动画 -->
      <div class="setting-item">
        <div class="setting-item-title">
          {{ t('layout.setting.transitionName') }}
        </div>
        <div class="setting-item-control" style="width: 110px">
          <el-select
            size="small"
            :model-value="transitionName"
            @update:modelValue="(val) => updateValue('transitionName', val)"
            :popper-options="{ strategy: 'fixed' }"
          >
            <el-option
              value="slide-right"
              :label="t('layout.setting.transitions.slideRight')"
            />
            <el-option
              value="slide-bottom"
              :label="t('layout.setting.transitions.slideBottom')"
            />
            <el-option
              value="zoom-in"
              :label="t('layout.setting.transitions.zoomIn')"
            />
            <el-option
              value="zoom-out"
              :label="t('layout.setting.transitions.zoomOut')"
            />
            <el-option
              value="fade"
              :label="t('layout.setting.transitions.fade')"
            />
          </el-select>
        </div>
      </div>
      <el-divider />
      <!-- 重置 -->
      <div class="setting-footer">
        <el-button size="small" class="ele-fluid" @click="resetSetting">
          {{ t('layout.setting.reset') }}
        </el-button>
      </div>
    </div>
    <div v-else-if="tabActive === 'layout'" class="setting-wrapper">
      <!-- 常用布局 -->
      <div class="setting-layout-list">
        <div
          v-for="item in predefineLayouts"
          :key="item.name"
          class="setting-layout-item"
          @click="handleLayoutItemClick(item)"
        >
          <div class="setting-layout-item-cover">
            <component v-if="item.cover" :is="item.cover" />
          </div>
          <div class="setting-layout-item-label">
            {{ t('layout.setting.layouts.' + item.name) }}
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="tabActive === 'skin'" class="setting-wrapper">
      <!-- 皮肤背景 -->
      <div class="setting-skin-list">
        <div class="setting-skin-item" @click="updateValue('skinConfig', null)">
          <div class="setting-skin-item-cover">
            <el-icon>
              <StopOutlined style="stroke-width: 2.6" />
            </el-icon>
          </div>
        </div>
        <div
          v-for="item in PREDEFINE_SKINS"
          :key="item.name"
          :class="[
            'setting-skin-item',
            {
              'is-active':
                !showUserSkin && skinConfig && skinConfig.name === item.name
            }
          ]"
          @click="updateValue('skinConfig', item)"
        >
          <div
            :style="{
              background: darkMode ? item.darkConfig?.cover : item.cover
            }"
            class="setting-skin-item-cover"
          ></div>
          <div class="setting-skin-item-label">
            {{ t('layout.setting.skins.' + item.name) }}
          </div>
        </div>
        <div
          :class="['setting-skin-item', { 'is-active': showUserSkin }]"
          @click="initUserSkin(true)"
        >
          <div
            class="setting-skin-item-cover"
            :style="{
              background: showUserSkin
                ? darkMode
                  ? userSkin.darkConfig?.wallpaper
                  : userSkin.wallpaper
                : void 0
            }"
          >
            <IconImage
              v-if="
                !showUserSkin ||
                !(darkMode
                  ? userSkin.darkConfig?.wallpaper
                  : userSkin.wallpaper)
              "
            />
          </div>
          <div class="setting-skin-item-label">
            {{ t('layout.setting.skins.custom') }}
          </div>
        </div>
      </div>
      <!-- 自定义皮肤背景 -->
      <template v-if="showUserSkin">
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.wallpaper') }}
          </div>
          <div class="setting-item-control">
            <el-upload
              action=""
              accept="image/*"
              :show-file-list="false"
              :before-upload="(file) => saveUserSkin(file)"
            >
              <el-icon
                class="setting-skin-item-cover setting-bg-upload"
                :style="{ background: userSkin.wallpaper }"
              >
                <PlusOutlined style="stroke-width: 3" />
              </el-icon>
            </el-upload>
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.maskColor') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.maskColor"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.headerBg') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.headerBg"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.sidebarBg') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.sidebarBg"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.cardBg') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.cardBg"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.overlayBg') }}
          </div>
          <div class="setting-item-control">
            <el-upload
              action=""
              accept="image/*"
              :show-file-list="false"
              :before-upload="(file) => saveUserSkin(file, true)"
            >
              <el-icon
                class="setting-skin-item-cover setting-bg-upload"
                :style="{ background: userSkin.overlayBg }"
              >
                <PlusOutlined style="stroke-width: 3" />
              </el-icon>
            </el-upload>
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.overlayMaskColor') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.overlayMaskColor"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <!-- 自定义暗黑模式皮肤背景 -->
        <el-divider />
        <ele-text type="placeholder" class="setting-title">
          {{ t('layout.setting.skinConfig.darkConfig') }}
        </ele-text>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.wallpaper') }}
          </div>
          <div class="setting-item-control">
            <el-upload
              action=""
              accept="image/*"
              :show-file-list="false"
              :before-upload="(file) => saveUserSkin(file, false, true)"
            >
              <el-icon
                class="setting-skin-item-cover setting-bg-upload"
                :style="{ background: userSkin.darkConfig.wallpaper }"
              >
                <PlusOutlined style="stroke-width: 3" />
              </el-icon>
            </el-upload>
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.maskColor') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.darkConfig.maskColor"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.headerBg') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.darkConfig.headerBg"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.sidebarBg') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.darkConfig.sidebarBg"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.cardBg') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.darkConfig.cardBg"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.overlayBg') }}
          </div>
          <div class="setting-item-control">
            <el-upload
              action=""
              accept="image/*"
              :show-file-list="false"
              :before-upload="(file) => saveUserSkin(file, true, true)"
            >
              <el-icon
                class="setting-skin-item-cover setting-bg-upload"
                :style="{ background: userSkin.darkConfig.overlayBg }"
              >
                <PlusOutlined style="stroke-width: 3" />
              </el-icon>
            </el-upload>
          </div>
        </div>
        <div class="setting-item">
          <div class="setting-item-title">
            {{ t('layout.setting.skinConfig.overlayMaskColor') }}
          </div>
          <div class="setting-item-control">
            <el-color-picker
              :show-alpha="true"
              v-model="userSkin.darkConfig.overlayMaskColor"
              class="setting-bg-picker"
              @change="saveUserSkin()"
            />
          </div>
        </div>
      </template>
    </div>
  </ele-drawer>
</template>

<script lang="ts" setup>
  import type { Component } from 'vue';
  import { ref, markRaw } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { storeToRefs } from 'pinia';
  import type { SkinConfig } from 'ele-admin-plus/es/utils/theme-util';
  import { isColorValue } from 'ele-admin-plus/es/utils/theme-util';
  import {
    CheckOutlined,
    StopOutlined,
    PlusOutlined
  } from '@/components/icons';
  import { useThemeStore, PREDEFINE_SKINS } from '@/store/modules/theme';
  import type { ThemeStateProp, ThemeState } from '@/store/modules/theme';
  import { doWithTransition } from '@/utils/common';
  import IconImage from './covers/icon-image.vue';
  import CoverDefault from './covers/cover-default.vue';
  import CoverMixedSidebar from './covers/cover-mixed-sidebar.vue';
  import CoverCompactSidebar from './covers/cover-compact-sidebar.vue';
  import CoverTabInHeader from './covers/cover-tab-in-header.vue';
  import CoverVerticalLayout from './covers/cover-vertical-layout.vue';
  import CoverTopBarLayout from './covers/cover-top-bar-layout.vue';
  import CoverLimitedWidth from './covers/cover-limited-width.vue';
  import CoverCardLayout from './covers/cover-card-layout.vue';
  import CoverCardSidebar from './covers/cover-card-sidebar.vue';
  import CoverCardMixedSidebar from './covers/cover-card-mixed-sidebar.vue';

  /** 抽屉是否打开 */
  const visible = defineModel({ type: Boolean });

  const { t } = useI18n();
  const themeStore = useThemeStore();

  const {
    tabBar,
    layout,
    sidebarLayout,
    headerStyle,
    sidebarStyle,
    mixSidebarStyle,
    tabStyle,
    fixedHeader,
    fixedSidebar,
    fixedBody,
    fluid,
    logoInHeader,
    transitionName,
    colorfulIcon,
    uniqueOpened,
    fixedHome,
    tabInHeader,
    menuItemTrigger,
    weakMode,
    darkMode,
    color,
    roundedTheme,
    footer,
    tabIcon,
    tabsCache,
    pageKeepAlive,
    skinConfig,
    responsive
  } = storeToRefs(themeStore);

  interface ThemeItem {
    /** 主题名称 */
    name: string;
    /** 主题颜色值 */
    value?: string;
    /** 主题抽屉中显示的色块颜色 */
    color?: string;
  }

  /** 预设主题颜色 */
  const themes = ref<ThemeItem[]>([
    { name: 'default', color: '#2f54eb' },
    { name: 'dust', value: '#5f80c7' },
    { name: 'sunset', value: '#faad14' },
    { name: 'volcano', value: '#f5686f' },
    { name: 'purple', value: '#9266f9' },
    { name: 'green', value: '#33cc99' },
    { name: 'geekblue', value: '#32a2d4' }
  ]);

  /** 颜色选择器预设颜色 */
  const predefineColors = ref<string[]>([
    '#f5222d',
    '#fa541c',
    '#fa8c16',
    '#faad14',
    '#a0d911',
    '#52c41a',
    '#13c2c2',
    '#1677ff',
    '#722ed1',
    '#eb2f96'
  ]);

  /** 选项卡选中 */
  const tabActive = ref('theme');

  /** 暗黑主题切换开关 */
  const darkSwitchRef = ref<HTMLElement | null>(null);

  /** 色弱模式切换开关 */
  const weakSwitchRef = ref<HTMLElement | null>(null);

  /** 颜色选择器选中颜色 */
  const colorPickerValue = ref<string | undefined>(void 0);

  interface LayoutItem {
    /** 布局名称 */
    name: string;
    /** 缩略图 */
    cover: string | Component;
    /** 布局配置 */
    config: Partial<ThemeState>;
  }

  /** 常用布局 */
  const predefineLayouts = ref<LayoutItem[]>([
    {
      name: 'default',
      cover: markRaw(CoverDefault),
      config: {
        collapse: false,
        compact: false,
        tabBar: true,
        layout: 'default',
        sidebarLayout: 'default',
        headerStyle: 'light',
        sidebarStyle: 'dark',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: false,
        tabInHeader: false,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'mixedSidebar',
      cover: markRaw(CoverMixedSidebar),
      config: {
        collapse: false,
        compact: false,
        tabBar: true,
        layout: 'default',
        sidebarLayout: 'mix',
        headerStyle: 'light',
        sidebarStyle: 'dark',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: false,
        tabInHeader: false,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'compactSidebar',
      cover: markRaw(CoverCompactSidebar),
      config: {
        collapse: true,
        compact: false,
        tabBar: true,
        layout: 'default',
        sidebarLayout: 'mix',
        headerStyle: 'light',
        sidebarStyle: 'dark',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: false,
        tabInHeader: false,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'tabInHeader',
      cover: markRaw(CoverTabInHeader),
      config: {
        collapse: false,
        compact: false,
        tabBar: true,
        layout: 'default',
        sidebarLayout: 'default',
        headerStyle: 'light',
        sidebarStyle: 'dark',
        mixSidebarStyle: 'light',
        tabStyle: 'tag',
        fixedBody: true,
        fluid: true,
        logoInHeader: false,
        tabInHeader: true,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'verticalLayout',
      cover: markRaw(CoverVerticalLayout),
      config: {
        collapse: false,
        compact: false,
        tabBar: true,
        layout: 'default',
        sidebarLayout: 'default',
        headerStyle: 'primary',
        sidebarStyle: 'light',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: true,
        tabInHeader: false,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'topBarLayout',
      cover: markRaw(CoverTopBarLayout),
      config: {
        collapse: false,
        compact: false,
        tabBar: false,
        layout: 'top',
        sidebarLayout: 'default',
        headerStyle: 'primary',
        sidebarStyle: 'light',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: true,
        tabInHeader: false,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'limitedWidth',
      cover: markRaw(CoverLimitedWidth),
      config: {
        collapse: false,
        compact: false,
        tabBar: false,
        layout: 'top',
        sidebarLayout: 'default',
        headerStyle: 'light',
        sidebarStyle: 'light',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: false,
        logoInHeader: true,
        tabInHeader: false,
        sidebarCustomStyle: null,
        sideboxCustomStyle: null,
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: null
      }
    },
    {
      name: 'cardLayout',
      cover: markRaw(CoverCardLayout),
      config: {
        collapse: false,
        compact: false,
        tabBar: true,
        layout: 'default',
        sidebarLayout: 'mix',
        headerStyle: 'primary',
        sidebarStyle: 'dark',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: false,
        tabInHeader: false,
        sidebarCustomStyle: {
          margin: '16px 0 16px 16px',
          borderRadius: '8px'
        },
        sideboxCustomStyle: {
          margin: '16px 0 16px 16px',
          borderRadius: '8px'
        },
        sideCustomStyle: null,
        headerCustomStyle: {
          margin: '16px 16px 0 16px',
          borderRadius: '8px 8px 0 0'
        },
        tabsCustomStyle: {
          margin: '0 16px 0 16px',
          borderRadius: '0 0 8px 8px'
        },
        contentCustomStyle: {
          clipPath: 'rect(16px 100% calc(100% - 16px) 16px round 8px 0 0 8px)'
        }
      }
    },
    {
      name: 'cardSidebar',
      cover: markRaw(CoverCardSidebar),
      config: {
        collapse: false,
        compact: false,
        tabBar: false,
        layout: 'default',
        sidebarLayout: 'default',
        headerStyle: 'light',
        sidebarStyle: 'light',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: true,
        tabInHeader: false,
        sidebarCustomStyle: {
          margin: '16px 0 16px 16px',
          borderRadius: '8px'
        },
        sideboxCustomStyle: {
          margin: '16px 0 16px 16px',
          borderRadius: '8px'
        },
        sideCustomStyle: null,
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: {
          clipPath: 'rect(16px 100% calc(100% - 16px) 16px round 8px 0 0 8px)'
        }
      }
    },
    {
      name: 'cardMixedSidebar',
      cover: markRaw(CoverCardMixedSidebar),
      config: {
        collapse: false,
        compact: false,
        tabBar: false,
        layout: 'default',
        sidebarLayout: 'mix',
        headerStyle: 'light',
        sidebarStyle: 'light',
        mixSidebarStyle: 'light',
        fixedBody: true,
        fluid: true,
        logoInHeader: true,
        tabInHeader: false,
        sidebarCustomStyle: {
          marginTop: '16px',
          marginBottom: '16px',
          borderRadius: '0 8px 8px 0'
        },
        sideboxCustomStyle: {
          margin: '16px 0 16px 16px',
          borderRadius: '8px 0 0 8px'
        },
        sideCustomStyle: {
          clipPath: 'rect(16px 100% calc(100% - 16px) 16px round 8px)'
        },
        headerCustomStyle: null,
        tabsCustomStyle: null,
        contentCustomStyle: {
          clipPath: 'rect(16px 100% calc(100% - 16px) 16px round 8px 0 0 8px)'
        }
      }
    }
  ]);

  interface UserSkin extends Omit<SkinConfig, 'darkConfig'> {
    darkConfig: Omit<SkinConfig, 'darkConfig'>;
  }

  /** 自定义皮肤背景 */
  const userSkin = ref<UserSkin>({ darkConfig: {} });

  /** 是否显示自定义皮肤背景 */
  const showUserSkin = ref(false);

  /** 初始化自定义主题 */
  const initUserSkin = (isCustom?: boolean) => {
    const cfg = skinConfig.value || {};
    const wallpaper = cfg.wallpaper || cfg.darkConfig?.wallpaper;
    const skin: UserSkin = {
      skinName: cfg.name,
      wallpaper: cfg.wallpaper || wallpaper,
      maskColor:
        (isColorValue(cfg.maskColor) ? cfg.maskColor : void 0) ||
        'rgba(255, 255, 255, 0.4)',
      headerBg: cfg.headerBg || 'rgba(255, 255, 255, 0.2)',
      sidebarBg: cfg.sidebarBg || 'rgba(255, 255, 255, 0.2)',
      cardBg: cfg.cardBg || 'rgba(255, 255, 255, 0.6)',
      overlayBg: cfg.overlayBg || 'rgba(255, 255, 255, 0.84)',
      overlayMaskColor:
        (isColorValue(cfg.overlayMaskColor) ? cfg.overlayMaskColor : void 0) ||
        'rgba(255, 255, 255, 0.4)',
      darkConfig: {
        wallpaper: cfg.darkConfig?.wallpaper || wallpaper,
        maskColor:
          (isColorValue(cfg.darkConfig?.maskColor)
            ? cfg.darkConfig?.maskColor
            : void 0) || 'rgba(0, 0, 0, 0.4)',
        headerBg: cfg.darkConfig?.headerBg || 'rgba(0, 0, 0, 0.2)',
        sidebarBg: cfg.darkConfig?.sidebarBg || 'rgba(0, 0, 0, 0.2)',
        cardBg: cfg.darkConfig?.cardBg || 'rgba(0, 0, 0, 0.4)',
        overlayBg: cfg.darkConfig?.overlayBg || 'hsla(0, 0%, 20%, 0.88)',
        overlayMaskColor:
          (isColorValue(cfg.darkConfig?.overlayMaskColor)
            ? cfg.darkConfig?.overlayMaskColor
            : void 0) || 'rgba(0, 0, 0, 0.4)'
      }
    };
    userSkin.value = skin;
    if (isCustom) {
      showUserSkin.value = true;
    } else if (!skinConfig.value) {
      showUserSkin.value = false;
    } else {
      showUserSkin.value = !cfg.name;
    }
  };

  /** 更新主题配置 */
  const updateValue = (prop: ThemeStateProp, value?: any) => {
    themeStore
      .setValue(prop, value)
      .then(() => {
        if (prop === 'skinConfig') {
          initUserSkin();
          if (value) {
            if (headerStyle.value !== 'light') {
              updateValue('headerStyle', 'light');
            }
            if (sidebarStyle.value !== 'light') {
              updateValue('sidebarStyle', 'light');
            }
            if (weakMode.value) {
              updateValue('weakMode', false);
            }
            /* if (value.name === PREDEFINE_SKINS[1]?.name && !darkMode.value) {
              updateValue('darkMode', true);
            } */
          }
        }
      })
      .catch((e) => {
        console.error(e);
      });
  };

  /** 初始化主题色选择器选中 */
  const initColorValue = () => {
    if (color.value && !themes.value.some((t) => t.value === color.value)) {
      colorPickerValue.value = color.value;
    } else {
      colorPickerValue.value = void 0;
    }
  };

  /** 切换主题色 */
  const updateColor = (val?: string | null) => {
    updateValue('color', val);
    initColorValue();
  };

  /** 切换暗黑模式 */
  const updateDarkMode = (val: boolean) => {
    doWithTransition(
      async () => updateValue('darkMode', val),
      darkSwitchRef.value?.querySelector?.('.el-switch__action'),
      !val
    );
  };

  /** 切换色弱模式 */
  const updateWeakMode = (val: boolean) => {
    doWithTransition(
      async () => updateValue('weakMode', val),
      weakSwitchRef.value?.querySelector?.('.el-switch__action'),
      !val,
      true
    );
  };

  /** 开关移动端响应式 */
  const updateResponsive = (val: boolean) => {
    updateValue('responsive', val);
    location.reload();
  };

  /** 重置 */
  const resetSetting = () => {
    const lastResponsive = responsive.value;
    themeStore
      .resetSetting()
      .then(() => {
        initColorValue();
        if (responsive.value !== lastResponsive) {
          location.reload();
        }
      })
      .catch((e) => {
        console.error(e);
      });
  };

  /** 保存自定义皮肤背景 */
  const saveUserSkin = (
    file?: File,
    isOverlayBg?: boolean,
    isDark?: boolean
  ) => {
    updateValue('skinConfig', async (storeWallpaperFile: any) => {
      const skin: UserSkin = {
        ...userSkin.value,
        darkConfig: { ...(userSkin.value.darkConfig || {}) }
      };
      if (file) {
        const fileId = await storeWallpaperFile(file);
        if (isDark) {
          if (isOverlayBg) {
            skin.darkConfig.overlayBg = fileId;
          } else {
            skin.darkConfig.wallpaper = fileId;
          }
        } else {
          if (isOverlayBg) {
            skin.overlayBg = fileId;
          } else {
            skin.wallpaper = fileId;
          }
        }
      }
      const wallpaper = skin.wallpaper || skin.darkConfig.wallpaper;
      if (!skin.wallpaper) {
        skin.wallpaper = wallpaper;
      }
      if (!skin.darkConfig.wallpaper) {
        skin.darkConfig.wallpaper = wallpaper;
      }
      return skin;
    });
    return false;
  };

  /** 切换常用布局 */
  const handleLayoutItemClick = (item: LayoutItem) => {
    Object.keys(item.config).forEach((key: ThemeStateProp) => {
      updateValue(key, item.config[key]);
    });
  };

  /** 抽屉打开事件 */
  const handleOpen = () => {
    initColorValue();
    initUserSkin();
  };
</script>

<style lang="scss" scoped>
  @use 'element-plus/theme-chalk/src/mixins/function.scss' as *;

  .setting-wrapper {
    flex: 1;
    overflow: auto;
    box-sizing: border-box;
    padding: 10px 12px 18px 12px;
    $header-primary-bg: linear-gradient(
      12deg,
      getCssVar('color-primary'),
      getCssVar('color-primary', 'light-3') 28%,
      getCssVar('color-primary', 'light-5') 46%,
      getCssVar('color-primary', 'light-3') 72%,
      getCssVar('color-primary') 92%,
      getCssVar('color-primary', 'dark-2')
    );

    .setting-title {
      font-size: 13px;
      padding: 0 6px;
      margin-bottom: 8px;
    }

    /* 主题风格 */
    .setting-theme {
      padding: 0 6px;
      box-sizing: border-box;

      & > div {
        width: 52px;
        height: 36px;
        line-height: 1;
        font-size: 16px;
        margin: 0 18px 18px 0;
        padding: 14px 0 0 24px;
        background: getCssVar('bg-color', 'page');
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
        border-radius: getCssVar('border-radius', 'base');
        transition: background-color 0.2s;
        box-sizing: border-box;
        display: inline-block;
        vertical-align: top;
        position: relative;
        overflow: hidden;
        cursor: pointer;

        &::before,
        &::after {
          content: '';
          width: 100%;
          height: 10px;
          background: getCssVar('bg-color');
          transition: background-color 0.2s;
          position: absolute;
          left: 0;
          top: 0;
        }

        &::after {
          width: 14px;
          height: 100%;
        }

        &.setting-side-dark::after,
        &.setting-head-dark::before,
        &.setting-layout-mix::before,
        &.setting-layout-mix::after {
          background: #262626;
        }

        &.setting-head-light::before,
        &.setting-head-dark::before,
        &.setting-head-primary::before {
          z-index: 1;
        }

        &.setting-side-light::before,
        &.setting-side-dark:not(.setting-layout-side)::before,
        &.setting-head-light::after,
        &.setting-head-dark::after,
        &.setting-head-primary::after {
          opacity: 0.5;
        }

        &.setting-head-primary::before {
          background: $header-primary-bg !important;
        }

        &.setting-layout-top {
          padding-left: 18px;

          &::after {
            display: none;
          }
        }
      }
    }

    /* 主题色选择 */
    .setting-colors {
      color: #fff;
      padding: 0 6px;
      margin-bottom: 18px;
      box-sizing: border-box;
      line-height: 0;
    }

    .setting-color-item {
      width: 20px;
      height: 20px;
      line-height: 20px;
      border-radius: getCssVar('border-radius', 'small');
      margin: 8px 8px 0 0;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      vertical-align: top;
      position: relative;
      text-align: center;
      cursor: pointer;

      .setting-color-tooltip {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
      }
    }

    .setting-colors .setting-color-item:last-child {
      margin-right: 0;
    }

    /* 主题配置项 */
    .setting-item {
      display: flex;
      align-items: center;
      padding: 0 6px;
      box-sizing: border-box;
      border-radius: getCssVar('border-radius', 'base');
      transition: background-color 0.2s;
      margin-bottom: 6px;

      .setting-item-title {
        flex: 1;
        line-height: 32px;
      }

      .setting-item-control {
        line-height: 1;
      }

      &:hover {
        background: getCssVar('fill-color', 'light');
      }
    }

    .el-divider.el-divider--horizontal {
      width: calc(100% - 12px);
      margin: 14px auto 10px auto;
      opacity: 0.6;
    }

    /* 颜色选择器 */
    .setting-colors :deep(.el-color-picker) {
      margin-top: 8px;
      line-height: 0;

      .el-color-picker__trigger {
        padding: 0;
        width: 20px;
        height: 20px;
        border: none;
      }

      .el-color-picker__color {
        border: none;
      }

      .el-color-picker__empty {
        background: conic-gradient(
          from 90deg at 50% 50%,
          rgb(255, 0, 0) -19.41deg,
          rgb(255, 0, 0) 18.76deg,
          rgb(255, 138, 0) 59.32deg,
          rgb(255, 230, 0) 99.87deg,
          rgb(20, 255, 0) 141.65deg,
          rgb(0, 163, 255) 177.72deg,
          rgb(5, 0, 255) 220.23deg,
          rgb(173, 0, 255) 260.13deg,
          rgb(255, 0, 199) 300.69deg,
          rgb(255, 0, 0) 340.59deg,
          rgb(255, 0, 0) 378.76deg
        );
        height: 100%;
        width: 100%;

        & > svg {
          display: none;
        }
      }
    }

    /* 常用布局 */
    .setting-layout-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 28px 18px;
      padding: 4px;
      box-sizing: border-box;
    }

    .setting-layout-item {
      height: 98px;
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      cursor: pointer;

      &.is-active {
        border-color: getCssVar('color-primary');
      }
    }

    .setting-layout-item-cover {
      flex: 1;
      width: 100%;
      box-sizing: border-box;
      background: getCssVar('bg-color', 'page');
      border-radius: getCssVar('border-radius', 'base');
      border: 1px solid getCssVar('border-color', 'light');
      transition: all 0.3s;
      overflow: hidden;

      &:hover {
        transform: scale(1.1);
      }
    }

    .setting-layout-item-label {
      flex-shrink: 0;
      line-height: 1;
      font-size: 13px;
      padding: 12px 0 0 0;
      box-sizing: border-box;
      text-align: center;
    }

    /* 皮肤背景 */
    .setting-skin-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      margin-bottom: 16px;
    }

    .setting-skin-item {
      height: 88px;
      padding: 6px;
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      border-radius: getCssVar('border-radius', 'base');
      border: 1px solid getCssVar('border-color');
      transition: all 0.2s;
      cursor: pointer;

      &:hover {
        border-color: getCssVar('color-primary');
        transform: translateY(-4px);
      }

      &.is-active {
        border-color: getCssVar('color-primary');
      }
    }

    .setting-skin-item-cover {
      flex: 1;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 34px;
      color: getCssVar('text-color', 'placeholder');
      border-radius: inherit;
      box-sizing: border-box;
    }

    .setting-skin-item-label {
      flex-shrink: 0;
      line-height: 1;
      font-size: 13px;
      padding: 6px 0 0 0;
      box-sizing: border-box;
      text-align: center;
    }

    /* 背景上传 */
    .setting-bg-upload {
      width: 28px;
      height: 28px;
      font-size: 15px;
      color: getCssVar('text-color', 'placeholder');
      border: 1px dashed getCssVar('border-color');
      border-radius: getCssVar('border-radius', 'base');
      box-sizing: border-box;
      transition: (color 0.2s, border-color 0.2s);

      &:hover {
        color: getCssVar('color-primary');
        border-color: getCssVar('color-primary');
      }
    }

    :deep(.el-upload) {
      display: flex;
    }

    /* 背景颜色选择 */
    :deep(.setting-bg-picker) {
      display: flex;
      margin-right: 1.5px;

      .el-color-picker__trigger {
        padding: 0;
        width: 25px;
        height: 25px;
      }

      .el-color-picker__color {
        border: none;
      }

      .el-color-picker__color-inner * {
        display: none;
      }
    }

    /* 布局缩略图 */
    :deep(div) {
      .setting-layout-cover-bg-dark {
        background: #262626;
      }

      .setting-layout-cover-bg-light {
        background: getCssVar('bg-color');
      }

      .setting-layout-cover-bg-primary {
        background: $header-primary-bg;
      }

      .setting-layout-cover-border-lighter {
        border-color: getCssVar('border-color', 'lighter');
      }

      .setting-layout-cover-bg-fill {
        background: getCssVar('fill-color');
      }

      .setting-layout-cover-bg-fill-light {
        background: getCssVar('border-color', 'light');
      }

      .setting-layout-cover-bg-fill-dark {
        background: getCssVar('border-color');
      }
    }
  }

  /* 选项卡 */
  .setting-segmented-wrapper {
    flex-shrink: 0;
    padding: 10px 14px 6px 14px;
    box-sizing: border-box;
  }

  /* 底栏 */
  .setting-footer {
    padding: 6px 6px 0 6px;
  }
</style>
