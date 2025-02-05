package kr.co.proten.llmops.core.helpers;

import java.util.List;
import java.util.Map;

public class TypeCastUtil {
    public static void printValueTypes(Object obj, String indent) {
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    System.out.println(indent + key + " : null");
                } else {
                    System.out.println(indent + key + " : " + value.getClass().getName());
                    // 재귀 호출: value가 Map이나 List면 내부도 순회
                    printValueTypes(value, indent + "  ");
                }
            }
        } else if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                if (element == null) {
                    System.out.println(indent + "[" + i + "] : null");
                } else {
                    System.out.println(indent + "[" + i + "] : " + element.getClass().getName());
                    // 재귀 호출: element가 Map이나 List면 내부도 순회
                    printValueTypes(element, indent + "  ");
                }
            }
        } else {
            // Map이나 List가 아닌 일반 객체는 추가 순회할 필요 없음
            // 예: String, Number, Boolean 등
        }
    }
}
