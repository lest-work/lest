import type { Locale } from './zh_CN';

export const zh_TW: Locale = {
  wakapi: {
    dashboard: {
      title: '編碼儀表盤',
      dau: 'DAU',
      wau: 'WAU',
      mau: 'MAU',
      activeRate: '活躍率',
      totalTime: '總編碼時長',
      activeUsers: '活躍用戶',
      trend7d: '近 7 天活躍趨勢',
      languageDist: '語言分布',
      topUsers: 'TOP 10 活躍用戶',
      teamRanking: '團隊編碼時長排名',
      heatmap: '編碼熱力地圖'
    },
    stats: {
      title: '統計詳情',
      project: '項目',
      language: '語言',
      editor: '編輯器',
      os: '操作系統',
      machine: '機器',
      totalTime: '總計時長',
      dailyAvg: '日均時長',
      longestDay: '最長單日',
      trend: '每日編碼時長趨勢',
      dist: '分布'
    },
    leaderboard: {
      title: '排行榜',
      rank: '排名',
      user: '用戶',
      codingTime: '編碼時長',
      percent: '占比'
    },
    team: {
      title: '團隊管理',
      create: '新建團隊',
      join: '加入團隊',
      name: '團隊名稱',
      description: '描述',
      visibility: '可見性',
      public: '公開',
      private: '私有',
      memberCount: '成員數',
      inviteCode: '邀請碼',
      createTime: '創建時間',
      leave: '離開團隊',
      edit: '編輯',
      members: '成員列表',
      addMember: '添加成員',
      remove: '移除',
      owner: '所有者',
      admin: '管理員',
      member: '成員',
      teamRanking: '團隊排行榜',
      noTeams: '暫無團隊'
    },
    alert: {
      title: '告警管理',
      rules: '告警規則',
      history: '告警歷史',
      ruleName: '規則名稱',
      type: '類型',
      condition: '條件',
      status: '狀態',
      triggerCount: '觸發次數',
      lastTriggered: '最近觸發',
      createRule: '新建規則',
      silence: '沉默提醒',
      codingTimeLow: '時長過低',
      codingTimeHigh: '時長異常高',
      inactive: '不活躍提醒',
      projectChange: '項目變更',
      notifyChannels: '通知渠道',
      email: '郵件',
      webhook: 'Webhook',
      inApp: '站內通知',
      triggered: '已觸發',
      acknowledged: '已確認',
      resolved: '已解決',
      ignored: '已忽略',
      acknowledge: '確認',
      resolve: '解決',
      ignore: '忽略'
    },
    summary: {
      title: '全平台編碼匯總',
      totalTime: '總計時長',
      activeUsers: '活躍用戶',
      activeProjects: '活躍項目',
      dailyAvg: '人均日均',
      project: '項目',
      language: '語言',
      editor: '編輯器',
      user: '用戶',
      export: '導出'
    },
    config: {
      alias: '別名管理',
      label: '標籤配置',
      languageMapping: '語言映射',
      scheduled: '定時任務',
      type: '類型',
      typeProject: '項目',
      typeLanguage: '語言',
      typeEditor: '編輯器',
      typeOS: '操作系統',
      typeMachine: '機器',
      matchKey: '匹配鍵',
      replaceTo: '替換為',
      labelName: '標籤名稱',
      matchProject: '匹配項目',
      color: '顏色',
      extension: '文件擴展名',
      mappingLanguage: '映射語言',
      taskName: '任務名稱',
      cron: 'Cron',
      lastRun: '上次',
      nextRun: '下次',
      executeNow: '立即執行',
      running: '執行中',
      idle: '空閒',
      disabled: '已禁用',
      executionLog: '執行日誌',
      startTime: '開始時間',
      endTime: '結束時間',
      duration: '耗時',
      message: '訊息',
      trigger: '觸發'
    },
    common: {
      today: '今天',
      yesterday: '昨天',
      last7Days: '近 7 天',
      last30Days: '近 30 天',
      thisWeek: '本週',
      thisMonth: '本月',
      thisYear: '本年',
      refresh: '重繪',
      save: '保存',
      cancel: '取消',
      confirm: '確定',
      delete: '刪除',
      edit: '編輯',
      add: '新建',
      search: '搜索',
      noData: '暫無數據',
      loading: '加載中'
    }
  },
  layout: {
    home: '主頁',
    header: {
      profile: '個人中心',
      password: '修改密碼',
      logout: '安全登出'
    },
    footer: {
      website: '官網',
      document: '檔案',
      authorization: '授權',
      copyright: 'Copyright © 2025 武漢易雲智科技有限公司'
    },
    logout: {
      title: '詢問',
      message: '確定要登出嗎?'
    },
    tabs: {
      reload: '重繪當前頁簽',
      close: '關閉當前頁簽',
      closeLeft: '關閉左側頁簽',
      closeRight: '關閉右側頁簽',
      closeOther: '關閉其他頁簽',
      closeAll: '關閉全部頁簽',
      fullscreen: '內容區域全屏',
      fullscreenExit: '退出內容全屏'
    },
    setting: {
      title: '主題風格設定',
      sideStyles: {
        dark: '暗色側欄',
        light: '亮色側欄'
      },
      headStyles: {
        light: '亮色頂欄',
        dark: '暗色頂欄',
        primary: '主色頂欄'
      },
      layoutStyles: {
        side: '左側選單佈局',
        top: '頂部選單佈局',
        mix: '混合選單佈局'
      },
      colors: {
        default: '拂曉藍',
        dust: '薄暮',
        sunset: '日暮',
        volcano: '火山',
        purple: '醬紫',
        green: '極光綠',
        geekblue: '極客藍'
      },
      darkMode: '開啟暗黑模式',
      roundedTheme: '開啟圓角主題',
      layout: '導航模式',
      sidebarLayout: '側欄雙排選單',
      mixSidebarStyle: '暗色二級側欄',
      fluid: '內容區域鋪滿',
      other: '其它配寘',
      more: '更多配寘',
      tab: '頁簽配寘',
      fixedHeader: '固定頂欄區域',
      fixedSidebar: '固定側欄區域',
      fixedBody: '內容區域滾動',
      logoInHeader: 'Logo置於頂欄',
      colorfulIcon: '側欄彩色圖標',
      uniqueOpened: '側欄排它展開',
      responsive: '移動端響應式',
      weakMode: '開啟色弱模式',
      showTabs: '開啟多頁簽欄',
      fixedHome: '固定主頁頁簽',
      tabInHeader: '頁簽置於頂欄',
      tabStyle: '頁簽顯示風格',
      tabStyles: {
        default: '默認',
        dot: '圓點',
        tag: '标签',
        card: '卡片'
      },
      tabIcon: '頁籤顯示圖標',
      tabsCache: '刷新保留頁籤',
      menuItemTrigger: '混合菜單分割',
      footer: '開啟全局頁腳',
      pageKeepAlive: '頁面切換緩存',
      transitionName: '路由切換動畫',
      transitions: {
        slideRight: '滑動消退',
        slideBottom: '底部消退',
        zoomIn: '放大漸變',
        zoomOut: '縮小漸變',
        fade: '淡入淡出'
      },
      reset: '重置',
      tabs: {
        theme: '主題設定',
        layout: '常用佈局',
        skin: '皮膚背景'
      },
      skins: {
        custom: '自定義',
        gradient: '彩色漸變',
        technology: '藍色科技',
        aesthetic: '唯美意境',
        cartoon: '可愛卡通'
      },
      skinConfig: {
        wallpaper: '頁面背景圖片',
        maskColor: '頁面背景蒙層',
        headerBg: '頂欄背景顏色',
        sidebarBg: '側欄背景顏色',
        cardBg: '卡片背景顏色',
        overlayBg: '彈框背景圖片',
        overlayMaskColor: '彈框背景蒙層',
        darkConfig: '暗黑模式'
      },
      layouts: {
        default: '常規佈局',
        mixedSidebar: '雙側欄佈局',
        compactSidebar: '緊湊側欄佈局',
        tabInHeader: '頁籤位於頂欄',
        cardSidebar: '卡片側欄佈局',
        cardMixedSidebar: '卡片雙側欄',
        cardLayout: '卡片風格佈局',
        verticalLayout: '上下佈局',
        topBarLayout: '頂欄佈局',
        limitedWidth: '內容固定寬度'
      }
    }
  },
  login: {
    title: '用戶登錄',
    username: '請輸入登入帳號',
    password: '請輸入登入密碼',
    code: '請輸入驗證碼',
    remember: '記住密碼',
    login: '登入',
    passwordType: '密碼登入',
    qrcodeType: '掃碼登入',
    refreshQrcode: '重繪二維碼'
  },
  list: {
    // 基础列表
    basic: {
      table: {
        avatar: '頭像',
        username: '用戶賬號',
        nickname: '用戶名',
        organizationName: '組織機構',
        phone: '手機號',
        email: '郵箱',
        roles: '角色',
        sexName: '性別',
        createTime: '創建時間',
        status: '狀態',
        action: '操作'
      }
    }
  }
};
