import { ref } from 'vue';
import type { Notification } from '@/api/notification';

type Listener = (notification: Notification) => void;

const client = ref<any>(null);
const connected = ref(false);
const listeners = new Set<Listener>();

export const notificationWsService = {
  get connected() {
    return connected.value;
  },

  connect(userId: string, token: string) {
    if (client.value?.connected) return;

    // Dynamic import to avoid loading stompjs if not needed
    import('stompjs').then((STOMP) => {
      const wsUrl = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/ws/notification`;
      const socket = new WebSocket(wsUrl);
      const stompClient = STOMP.default.over(socket);

      stompClient.connect(
        { Authorization: token },
        () => {
          connected.value = true;
          stompClient.subscribe(
            `/user/${userId}/queue/notifications`,
            (frame: any) => {
              try {
                const notification: Notification = JSON.parse(frame.body);
                listeners.forEach((fn) => fn(notification));
              } catch {
                // ignore parse errors
              }
            }
          );
        },
        () => {
          connected.value = false;
        }
      );

      socket.onclose = () => {
        connected.value = false;
        client.value = null;
      };

      client.value = stompClient;
    });
  },

  disconnect() {
    if (client.value) {
      client.value.disconnect();
      client.value = null;
      connected.value = false;
    }
  },

  /** 添加实时通知监听器，返回取消函数 */
  onNotification(listener: Listener): () => void {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
};
