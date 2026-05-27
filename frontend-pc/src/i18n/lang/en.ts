import type { Locale } from './zh_CN';

export const en: Locale = {
  wakapi: {
    dashboard: {
      title: 'Coding Dashboard',
      dau: 'DAU',
      wau: 'WAU',
      mau: 'MAU',
      activeRate: 'Active Rate',
      totalTime: 'Total Coding Time',
      activeUsers: 'Active Users',
      trend7d: 'Last 7 Days Trend',
      languageDist: 'Language Distribution',
      topUsers: 'TOP 10 Active Users',
      teamRanking: 'Team Coding Time Ranking',
      heatmap: 'Coding Heatmap'
    },
    stats: {
      title: 'Statistics Detail',
      project: 'Project',
      language: 'Language',
      editor: 'Editor',
      os: 'OS',
      machine: 'Machine',
      totalTime: 'Total Time',
      dailyAvg: 'Daily Average',
      longestDay: 'Longest Day',
      trend: 'Daily Coding Time Trend',
      dist: 'Distribution'
    },
    leaderboard: {
      title: 'Leaderboard',
      rank: 'Rank',
      user: 'User',
      codingTime: 'Coding Time',
      percent: 'Percent'
    },
    team: {
      title: 'Team Management',
      create: 'Create Team',
      join: 'Join Team',
      name: 'Team Name',
      description: 'Description',
      visibility: 'Visibility',
      public: 'Public',
      private: 'Private',
      memberCount: 'Members',
      inviteCode: 'Invite Code',
      createTime: 'Created',
      leave: 'Leave Team',
      edit: 'Edit',
      members: 'Member List',
      addMember: 'Add Member',
      remove: 'Remove',
      owner: 'Owner',
      admin: 'Admin',
      member: 'Member',
      teamRanking: 'Team Leaderboard',
      noTeams: 'No teams yet'
    },
    alert: {
      title: 'Alert Management',
      rules: 'Alert Rules',
      history: 'Alert History',
      ruleName: 'Rule Name',
      type: 'Type',
      condition: 'Condition',
      status: 'Status',
      triggerCount: 'Triggers',
      lastTriggered: 'Last Triggered',
      createRule: 'Create Rule',
      silence: 'Silence Alert',
      codingTimeLow: 'Low Coding Time',
      codingTimeHigh: 'High Coding Time',
      inactive: 'Inactive Alert',
      projectChange: 'Project Change',
      notifyChannels: 'Notify Channels',
      email: 'Email',
      webhook: 'Webhook',
      inApp: 'In-App',
      triggered: 'Triggered',
      acknowledged: 'Acknowledged',
      resolved: 'Resolved',
      ignored: 'Ignored',
      acknowledge: 'Acknowledge',
      resolve: 'Resolve',
      ignore: 'Ignore'
    },
    summary: {
      title: 'Platform Coding Summary',
      totalTime: 'Total Time',
      activeUsers: 'Active Users',
      activeProjects: 'Active Projects',
      dailyAvg: 'Daily Average',
      project: 'Project',
      language: 'Language',
      editor: 'Editor',
      user: 'User',
      export: 'Export'
    },
    config: {
      alias: 'Alias Management',
      label: 'Label Configuration',
      languageMapping: 'Language Mapping',
      scheduled: 'Scheduled Tasks',
      type: 'Type',
      typeProject: 'Project',
      typeLanguage: 'Language',
      typeEditor: 'Editor',
      typeOS: 'OS',
      typeMachine: 'Machine',
      matchKey: 'Match Key',
      replaceTo: 'Replace To',
      labelName: 'Label Name',
      matchProject: 'Match Project',
      color: 'Color',
      extension: 'Extension',
      mappingLanguage: 'Language',
      taskName: 'Task Name',
      cron: 'Cron',
      lastRun: 'Last Run',
      nextRun: 'Next Run',
      executeNow: 'Execute Now',
      running: 'Running',
      idle: 'Idle',
      disabled: 'Disabled',
      executionLog: 'Execution Log',
      startTime: 'Start Time',
      endTime: 'End Time',
      duration: 'Duration',
      message: 'Message',
      trigger: 'Trigger'
    },
    common: {
      today: 'Today',
      yesterday: 'Yesterday',
      last7Days: 'Last 7 Days',
      last30Days: 'Last 30 Days',
      thisWeek: 'This Week',
      thisMonth: 'This Month',
      thisYear: 'This Year',
      refresh: 'Refresh',
      save: 'Save',
      cancel: 'Cancel',
      confirm: 'Confirm',
      delete: 'Delete',
      edit: 'Edit',
      add: 'Create',
      search: 'Search',
      noData: 'No Data',
      loading: 'Loading'
    }
  },
  layout: {
    home: 'Home',
    header: {
      profile: 'Profile',
      password: 'Password',
      logout: 'SignOut'
    },
    footer: {
      website: 'Website',
      document: 'Document',
      authorization: 'Authorization',
      copyright: 'Copyright © 2025 Wuhan EClouds Technology Co., Ltd'
    },
    logout: {
      title: 'Confirm',
      message: 'Are you sure you want to log out?'
    },
    tabs: {
      reload: 'Refresh',
      close: 'Close',
      closeLeft: 'Close Left',
      closeRight: 'Close Right',
      closeOther: 'Close Other',
      closeAll: 'Close All',
      fullscreen: 'Fullscreen',
      fullscreenExit: 'Fullscreen'
    },
    setting: {
      title: 'Theme Setting',
      sideStyles: {
        dark: 'Dark Sidebar',
        light: 'Light Sidebar'
      },
      headStyles: {
        light: 'Light Header',
        dark: 'Dark Header',
        primary: 'Primary Header'
      },
      layoutStyles: {
        side: 'Side Menu Layout',
        top: 'Top Menu Layout',
        mix: 'Mix Menu Layout'
      },
      colors: {
        default: 'Daybreak Blue',
        dust: 'Dust Blue',
        sunset: 'Sunset Orange',
        volcano: 'Volcano',
        purple: 'Golden Purple',
        green: 'Polar Green',
        geekblue: 'Geek Blue'
      },
      darkMode: 'Dark Mode',
      roundedTheme: 'Rounded Theme',
      layout: 'Navigation Mode',
      sidebarLayout: 'Sidebar Double Menu',
      mixSidebarStyle: 'Dark Secondary Sidebar',
      fluid: 'Full Body Width',
      other: 'Other Setting',
      more: 'More Setting',
      tab: 'Tab Setting',
      fixedHeader: 'Fixed Header',
      fixedSidebar: 'Fixed Sidebar',
      fixedBody: 'Scrollbar In Content',
      logoInHeader: 'Logo In Header',
      colorfulIcon: 'Colorful Icon',
      uniqueOpened: 'Menu Unique Open',
      responsive: 'Responsive',
      weakMode: 'Weak Mode',
      showTabs: 'Show Tabs',
      fixedHome: 'Fixed Home Tab',
      tabInHeader: 'Tab In Header',
      tabStyle: 'Tab Style',
      tabStyles: {
        default: 'Default',
        dot: 'Dot',
        tag: 'Tag',
        card: 'Card'
      },
      tabIcon: 'Tab Icon',
      tabsCache: 'Tabs Cache',
      menuItemTrigger: 'Menu Division',
      footer: 'Global Footer',
      pageKeepAlive: 'Page Keep Alive',
      transitionName: 'Transition',
      transitions: {
        slideRight: 'Slide Right',
        slideBottom: 'Slide Bottom',
        zoomIn: 'Zoom In',
        zoomOut: 'Zoom Out',
        fade: 'Fade'
      },
      reset: 'Reset',
      tabs: {
        theme: 'Theme',
        layout: 'Layout',
        skin: 'Skin'
      },
      skins: {
        custom: 'Custom',
        gradient: 'Gradient',
        technology: 'Technology',
        aesthetic: 'Aesthetic',
        cartoon: 'Cartoon'
      },
      skinConfig: {
        wallpaper: 'Page Wallpaper',
        maskColor: 'Page Wallpaper Mask',
        headerBg: 'Header Background',
        sidebarBg: 'Sidebar Background',
        cardBg: 'Card Background',
        overlayBg: 'Overlay Background',
        overlayMaskColor: 'Overlay Background Mask',
        darkConfig: 'Dark Theme'
      },
      layouts: {
        default: 'Default',
        mixedSidebar: 'Mixed Sidebar',
        compactSidebar: 'Compact Sidebar',
        tabInHeader: 'Tab In Header',
        cardSidebar: 'Card Sidebar',
        cardMixedSidebar: 'Card Mixed Side',
        cardLayout: 'Card Layout',
        verticalLayout: 'Vertical Layout',
        topBarLayout: 'Top Bar Layout',
        limitedWidth: 'Limited Width'
      }
    }
  },
  login: {
    title: 'User Login',
    username: 'please input username',
    password: 'please input password',
    code: 'please input code',
    remember: 'remember',
    login: 'Login',
    passwordType: 'Password',
    qrcodeType: 'QR Code',
    refreshQrcode: 'Refresh'
  },
  list: {
    // 基础列表
    basic: {
      table: {
        avatar: 'Avatar',
        username: 'Username',
        nickname: 'Nickname',
        organizationName: 'Organization',
        phone: 'Phone',
        email: 'Email',
        roles: 'Roles',
        sexName: 'Sex',
        createTime: 'CreateTime',
        status: 'Status',
        action: 'Action'
      }
    }
  }
};
