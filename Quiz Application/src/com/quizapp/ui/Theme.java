package com.quizapp.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralizes fonts and colors so every screen feels consistent.
 */
public final class Theme {
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color PANEL = Color.WHITE;
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color TEXT = new Color(17, 24, 39);
    public static final Color MUTED_TEXT = new Color(107, 114, 128);
    public static final Color BORDER = new Color(209, 213, 219);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(245, 158, 11);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font QUESTION_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private Theme() {
    }
}
