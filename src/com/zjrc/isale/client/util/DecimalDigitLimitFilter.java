package com.zjrc.isale.client.util;

import android.text.Spanned;
import android.text.method.DigitsKeyListener;

public class DecimalDigitLimitFilter  extends DigitsKeyListener {

    private final int UPPER_POINT_DIGITS_NUM;
    private final int LOWER_POINT_DIGITS_NUM;

    public DecimalDigitLimitFilter(int upperDigitsNum, int lowerDigitsNum) {
        super(false, true);
        UPPER_POINT_DIGITS_NUM = upperDigitsNum < 0 ? 0 : upperDigitsNum;
        LOWER_POINT_DIGITS_NUM = lowerDigitsNum < 0 ? 0 : lowerDigitsNum;
    }

    /**
     * このメソッドは、dest の dstart から dend の部分が source の start から end に置き換えられるときに呼ばれる
     * 変換が適切な場合は null を返し、変換が不適切な場合は代わりの文字（空文字を含む）を返す
     * 例えば数字だけ入力可能な場合は、数字以外の文字が入力されたら空文字を返すように、数字が入力されたら null を返すようにする
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence cs = super.filter(source, start, end, dest, dstart, dend);
        // 不適切な文字が入力された場合は桁数をチェックしない
        if (cs != null) {
            return cs;
        }

        // 对于复制只截取2位小数
        if (end - start > 1) {
            int i = source.toString().indexOf(".");
            if (i >= 0) {
                return source.subSequence(0, i + 2);
            } else {
                return source;
            }
        }

        String s = dest.toString();
        int i = s.indexOf(".");
        if (i >= 0) {
            // 小数点が入力されている場合
            if (dstart <= i && i < dend) {
                // 小数点を含む部分を変更
                if (!source.equals(".")) {
                    // 小数点を削除する場合
                    if (dest.length() - (dend - dstart) + (end - start) <= UPPER_POINT_DIGITS_NUM) {
                        return null;
                    } else {
                        return ".";
                    }
                } else {
                    // 小数点を小数点に置き換える場合
                    return null;
                }
            }

            // 小数点を含まない部分を変更
            if (dstart <= i) {
                // 小数点以上を変更する場合
                if (i - (dend - dstart) + (end - start) <= UPPER_POINT_DIGITS_NUM) {
                    return null;
                } else {
                    return "";
                }
            } else {
                // 小数点以下を変更する場合
                if (dest.length() - (dend - dstart) + (end - start) - i - 1 <= LOWER_POINT_DIGITS_NUM) {
                    return null;
                } else {
                    return "";
                }
            }
        } else {
            // 小数点が入力されていない場合
            if (source.equals(".")) {
                // 小数点を入力する場合
                // 例）upper = 4, lower = 2 の時に 1111 → 1.111 や 111 → .111 を許可しない
                if (dest.length() - dend <= LOWER_POINT_DIGITS_NUM) {
                    return null;
                } else {
                    return "";
                }
            } else {
                // 数字を入力する場合
                if (dest.length() - (dend - dstart) + (end - start) <= UPPER_POINT_DIGITS_NUM) {
                    return null;
                } else {
                    return "";
                }
            }
        }
    }
}
