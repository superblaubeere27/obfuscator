package me.superblaubeere27.jobf.ui;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import me.superblaubeere27.jobf.JObf;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * @author Rodrigo Garcia Lima (email: rodgarcialima@gmail.com | github: rodgarcialima)
 *
 * @see ch.qos.logback.core.AppenderBase
 */
public class Appender extends AppenderBase<ILoggingEvent> {

  /**
   * Cada nível de log tem um estilo próprio
   */
  private static final SimpleAttributeSet ERROR_ATT;
  private static final SimpleAttributeSet WARN_ATT;
  private static final SimpleAttributeSet INFO_ATT;
  private static final SimpleAttributeSet DEBUG_ATT;
  private static final SimpleAttributeSet TRACE_ATT;
  private static final SimpleAttributeSet RESTO_ATT;

  /**
   * Definição dos estilos de log
   */
  static {
    // ERROR
    ERROR_ATT = new SimpleAttributeSet();
    ERROR_ATT.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
    ERROR_ATT.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
    ERROR_ATT.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(153, 0, 0));

    // WARN
    WARN_ATT = new SimpleAttributeSet();
    WARN_ATT.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
    WARN_ATT.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
    WARN_ATT.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(153, 76, 0));

    // INFO
    INFO_ATT = new SimpleAttributeSet();
    INFO_ATT.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
    INFO_ATT.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
    INFO_ATT.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0, 0, 153));

    // DEBUG
    DEBUG_ATT = new SimpleAttributeSet();
    DEBUG_ATT.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
    DEBUG_ATT.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
    DEBUG_ATT.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(64, 64, 64));

    // TRACE
    TRACE_ATT = new SimpleAttributeSet();
    TRACE_ATT.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
    TRACE_ATT.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
    TRACE_ATT.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(153, 0, 76));

    // RESTO
    RESTO_ATT = new SimpleAttributeSet();
    RESTO_ATT.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
    RESTO_ATT.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
    RESTO_ATT.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0, 0, 0));
  }

  /**
   * log message formatter
   */
  private PatternLayout patternLayout;

  /**
   * Code copied from {@link JTextPane#getLineCount()}
   *
   * @param textArea
   *          start of line count
   * @return count of lines &gt; 0
   */
  private int getLineCount(final JTextPane textArea) {

    return textArea.getDocument().getDefaultRootElement().getElementCount();
  }

  /**
   * Code copied from {@link JTextPane#getLineEndOffset(int)}
   * @param textArea
   *          offset for TextArea
   * @param line
   *          the line &gt;= 0
   * @return the offset &gt;= 0
   * @throws BadLocationException
   *           Thrown if the line is
   *           less than zero or greater or equal to the number of
   *           lines contained in the document (as reported by
   *           getLineCount)
   */
  private int getLineEndOffset(final JTextPane textArea, final int line) throws BadLocationException {

    final int lineCount = getLineCount(textArea);
    if (line < 0) {
      throw new BadLocationException("Negative line", -1);
    } else if (line >= lineCount) {
      throw new BadLocationException("No such line", textArea.getDocument().getLength() + 1);
    } else {
      final Element map = textArea.getDocument().getDefaultRootElement();
      final Element lineElem = map.getElement(line);
      final int endOffset = lineElem.getEndOffset();
      // hide the implicit break at the end of the document
      return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
    }
  }

  /**
   * Code copied from  {@link JTextPane#replaceRange(String, int, int)}<br>
   *
   * Replaces text from the indicated start to end position with the
   * new text specified. Does nothing if the model is null. Simply
   * does a delete if the new string is null or empty.<br>
   *
   * @param textPane
   *          de onde quero substituir o texto
   * @param str
   *          the text to use as the replacement
   * @param start
   *          the start position &gt;= 0
   * @param end
   *          the end position &gt;= start
   * @exception IllegalArgumentException
   *              if part of the range is an invalid position in the model
   */
  private void replaceRange(final JTextPane textPane, final String str, final int start, final int end)
      throws IllegalArgumentException {

    if (end < start) {
      throw new IllegalArgumentException("end before start");
    }
    final Document doc = textPane.getDocument();
    if (doc != null) {
      try {
        if (doc instanceof AbstractDocument) {
          ((AbstractDocument) doc).replace(start, end - start, str, null);
        } else {
          doc.remove(start, end - start);
          doc.insertString(start, str, null);
        }
      } catch (final BadLocationException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
  }

  @Override
  protected void append(final ILoggingEvent event) {

    // Formats log message
    final String formattedMsg = patternLayout.doLayout(event);

    // Safe way to update JTextPane
    SwingUtilities.invokeLater(() -> {
      // Alias for JTextPane in the application frame
      final JTextPane textArea = JObf.getGui();
      if (textArea == null) {
        return;
      }
      try {
        final int limite = 1000;
        final int apaga = 200;
        if (textArea.getDocument().getDefaultRootElement().getElementCount() > limite) {
          final int end = getLineEndOffset(textArea, apaga);
          replaceRange(textArea, null, 0, end);
        }

        if (event.getLevel() == Level.ERROR) {
          textArea.getDocument().insertString(textArea.getDocument().getLength(), formattedMsg,
              ERROR_ATT);
        } else if (event.getLevel() == Level.WARN) {
          textArea.getDocument().insertString(textArea.getDocument().getLength(), formattedMsg,
              WARN_ATT);
        } else if (event.getLevel() == Level.INFO) {
          textArea.getDocument().insertString(textArea.getDocument().getLength(), formattedMsg,
              INFO_ATT);
        } else if (event.getLevel() == Level.DEBUG) {
          textArea.getDocument().insertString(textArea.getDocument().getLength(), formattedMsg,
              DEBUG_ATT);
        } else if (event.getLevel() == Level.TRACE) {
          textArea.getDocument().insertString(textArea.getDocument().getLength(), formattedMsg,
              TRACE_ATT);
        } else {
          textArea.getDocument().insertString(textArea.getDocument().getLength(), formattedMsg,
              RESTO_ATT);
        }

      } catch (final BadLocationException e) {
        //Nothing to do
      }

      // Scroll to last line
      textArea.setCaretPosition(textArea.getDocument().getLength());
    });
  }

  @Override
  public void start() {

    patternLayout = new PatternLayout();
    patternLayout.setContext(getContext());
    patternLayout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
    patternLayout.start();

    super.start();
  }
}
