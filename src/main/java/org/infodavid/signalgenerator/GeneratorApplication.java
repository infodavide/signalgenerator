package org.infodavid.signalgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.text.TextAction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.infodavid.signalgenerator.configuration.Configuration;
import org.infodavid.signalgenerator.generator.Generator;
import org.infodavid.signalgenerator.generator.GeneratorRegistry;
import org.jdesktop.swingx.JXDatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focussia.util.concurrency.ThreadUtils;
import com.focussia.util.jackson.XmlUtils;
import com.focussia.util.logging.LoggingUtils;
import com.focussia.util.swing.SwingAppender;
import com.focussia.util.swing.SwingUtils;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * The Class GeneratorApplication.
 */
public class GeneratorApplication {

    /** The Constant CLEAR. */
    private static final String CLEAR = "Clear";

    /** The Constant CONFIGURATION_FILE. */
    private static final String CONFIGURATION_FILE = "generator.xml";

    /** The Constant COPY. */
    private static final String COPY = "Copy";

    /** The Constant DATE_FORMATTER. */
    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorApplication.class);

    /**
     * The main method.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        FlatLightLaf.install();
        final JWindow splash = new JWindow();

        try {
            splash.getContentPane().setBackground(Color.white);
            splash.getContentPane().add(new JLabel("", new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("splash.png"))), SwingConstants.CENTER));
        } catch (final Exception e) {
            LOGGER.warn("Cannot load image", e); // NOSONAR
        }

        splash.setSize(300, 200);
        SwingUtils.getInstance().centerOnScreen(splash);
        splash.setVisible(true);
        EventQueue.invokeLater(() -> {
            try {
                final GeneratorApplication application = new GeneratorApplication();
                final StringBuilder buffer = new StringBuilder();
                LoggingUtils.getInstance().log(application.configuration.getTitle(), Collections.emptyMap(), true, buffer);
                LOGGER.info(buffer.toString()); // NOSONAR
                application.getFrame().setVisible(true);
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                splash.setVisible(false);
            }
        });
    }

    /**
     * Builds the popup menu.
     * @param parent the parent
     * @return the popup menu
     */
    private static JPopupMenu buildPopupMenu(final JComponent parent) {
        final JPopupMenu result = new JPopupMenu();
        TextAction action = new DefaultEditorKit.CopyAction();
        action.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        action.putValue(javax.swing.Action.NAME, COPY);
        result.add(new JMenuItem(action));
        result.add(new JSeparator());
        action = new TextAction(CLEAR) {
            private static final long serialVersionUID = 514121915658824319L;

            @SuppressWarnings("rawtypes")
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (parent instanceof JTextComponent) {
                    ((JTextComponent) parent).setText("");
                } else if (parent instanceof JList) {
                    ((DefaultListModel) ((JList) parent).getModel()).clear();
                }
            }
        };
        action.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        action.putValue(javax.swing.Action.NAME, CLEAR);
        result.add(new JMenuItem(action));

        return result;
    }

    /** The configuration. */
    private Configuration configuration = new Configuration();

    /** The configuration button. */
    private final JButton configurationButton = new JButton("Configuration");

    /** The configuration file. */
    private File configurationFile;

    /** The end field. */
    private final JXDatePicker endField = new JXDatePicker();

    /** The executor. */
    private final ScheduledExecutorService executor = ThreadUtils.getInstance().newScheduledExecutorService(this.getClass(), LOGGER, 2);

    /** The frame. */
    private final JFrame frame;

    /** The generate button. */
    private final JButton generateButton = new JButton("Generate");

    /** The generators combobox. */
    private final JComboBox<Generator> generatorsCombo = new JComboBox<>(new DefaultComboBoxModel<>());

    /** The line pattern field. */
    private final JTextField linePatternField = new JTextField();

    /** The output file field. */
    private final JTextField outputFileField = new JTextField();

    /** The properties modification date. */
    private long propertiesModificationDate = -1;

    /** The quit button. */
    private final JButton quitButton = new JButton("Quit");

    /** The start field. */
    private final JXDatePicker startField = new JXDatePicker();

    /** The step field. */
    private final JSpinner stepField = new JSpinner(new SpinnerNumberModel(500, Constants.MINIMUM_STEP, Constants.MAXIMUM_STEP, 100));

    /** The tabbed pane. */
    private final JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Instantiates a new application.
     * @throws NotBoundException      the not bound exception
     * @throws IOException            Signals that an I/O exception has occurred.
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public GeneratorApplication() throws NotBoundException, IOException, InstantiationException, IllegalAccessException {
        frame = initializeFrame();
        SwingUtilities.invokeLater(() -> {
            try {
                readConfiguration();
            } catch (final IOException e) {
                LOGGER.warn("Cannot apply configuration", e);
            }
        });
    }

    /**
     * Gets the frame.
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Enable generation.
     */
    private void enableGeneration() {
        generateButton.setEnabled(startField.getDate() != null && endField.getDate() != null && StringUtils.isNotEmpty(outputFileField.getText()));
    }

    /**
     * Enable inputs.
     * @param flag the flag
     */
    private void enableInputs(final boolean flag) {
        generatorsCombo.setEnabled(flag);
        startField.setEnabled(flag);
        endField.setEnabled(flag);
        stepField.setEnabled(flag);
        linePatternField.setEnabled(flag);
        outputFileField.setEnabled(flag);
        configurationButton.setEnabled(flag);
        generateButton.setEnabled(flag && startField.getDate() != null && endField.getDate() != null && StringUtils.isNotEmpty(outputFileField.getText()));
        tabbedPane.setEnabled(flag);
    }

    /**
     * Exit.
     */
    private void exit() {
        if (configurationFile == null) {
            configurationFile = new File(CONFIGURATION_FILE);
        }

        if (configurationFile.exists() && propertiesModificationDate > 0 && propertiesModificationDate != configurationFile.lastModified()) {
            LOGGER.warn("Configuration file {} has been edited during the execution of the program, skipping saving.", configurationFile.getAbsolutePath());
        } else {
            LOGGER.info("Saving configuration in file: {}", configurationFile.getAbsolutePath());
            configuration.setComment("Generated by Signal generator on " + DATE_FORMATTER.format(System.currentTimeMillis()));

            try (FileOutputStream out = new FileOutputStream(configurationFile); OutputStreamWriter writer = new OutputStreamWriter(out)) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE xml>\n");
                writer.write(XmlUtils.getInstance().toXml(configuration, "\n"));
            } catch (final IOException e) {
                LOGGER.error("Cannot save configuration", e);
            }
        }

        try {
            ThreadUtils.getInstance().shutdown(executor);
        } catch (final InterruptedException e) { // NOSONAR Exit at the end
            LOGGER.warn("Thread interrupted", e);
        }

        System.exit(0);
    }

    /**
     * Generate.
     */
    @SuppressWarnings("boxing")
    private void generate() {
        final String linePattern = linePatternField.getText();

        if (!linePattern.contains("${")) {
            LOGGER.error("Invalid line pattern: {}", linePattern);

            return;
        }

        final long step = ((Number) stepField.getValue()).longValue();
        final long start = startField.getDate().getTime();
        final long end = endField.getDate().getTime();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generating using start: {} to: {} using step: {}ms", start, end, step);
        }

        final Generator generator = (Generator) generatorsCombo.getSelectedItem();
        setCursor(Cursor.WAIT_CURSOR);
        enableInputs(false);
        EventQueue.invokeLater(() -> {
            try (Writer writer = Files.newBufferedWriter(Paths.get(outputFileField.getText()), StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
                final ValuesWriter w;

                if (linePattern.contains("${d}")) {
                    w = (t, v) -> {
                        if (v != null) {
                            String line = linePattern.replace("${t}", String.valueOf(t));
                            line = line.replace("${d}", Constants.DATETIME_FORMAT.format(t));
                            writer.write(line.replace("${v}", v.toString()) + '\n');
                        }
                    };
                } else {
                    w = (t, v) -> {
                        final String line = linePattern.replace("${t}", String.valueOf(t));
                        writer.write(line.replace("${v}", v.toString()) + '\n');
                    };
                }

                generator.generate(start, end, step, w);
            } catch (final IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                enableInputs(true);
                setCursor(Cursor.DEFAULT_CURSOR);
            }
        });
    }

    /**
     * Initialize the frame.
     * @return the frame
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    @SuppressWarnings("unchecked")
    private JFrame initializeFrame() throws InstantiationException, IllegalAccessException { // NOSONAR
        LOGGER.info("Initializing frame");
        final JFrame result = new JFrame();
        result.setTitle(configuration.getTitle());
        result.getContentPane().setLayout(new BorderLayout());

        try (InputStream in = getClass().getResourceAsStream("/icons/Focussia_icon.png")) {
            result.setIconImage(ImageIO.read(in));
        } catch (final IOException e) {
            LOGGER.warn("Cannot set the icon on the window", e);
        }

        result.addWindowListener(new WindowAdapter() {
            /*
             * (non-javadoc)
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e) {
                super.windowClosing(e);
                exit();
            }
        });

        // Prepare signal settings panel
        final JPanel signalSettingsPanel = new JPanel(new GridBagLayout());
        signalSettingsPanel.setBorder(BorderFactory.createEmptyBorder());
        signalSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        signalSettingsPanel.setName("Signal settings");
        // Settings panel
        final JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder());
        settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.setName("Settings");
        tabbedPane.addTab(settingsPanel.getName(), settingsPanel);
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridwidth = 1;
        settingsPanel.add(new JLabel("Signal"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        final DefaultComboBoxModel<Generator> generatorsComboModel = (DefaultComboBoxModel<Generator>) generatorsCombo.getModel();

        for (final Class<? extends Generator> clazz : GeneratorRegistry.getInstance().getGenerators()) {
            generatorsComboModel.addElement(clazz.newInstance());
        }
        generatorsCombo.setRenderer(new GeneratorComboBoxRenderer());
        generatorsCombo.addActionListener(e -> {
            final Generator generator = (Generator) generatorsCombo.getSelectedItem();
            signalSettingsPanel.removeAll();
            generator.updateUi(signalSettingsPanel);
        });
        settingsPanel.add(generatorsCombo, constraints);
        generatorsCombo.setSelectedIndex(0);
        // Start date
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        settingsPanel.add(new JLabel("Start date"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        settingsPanel.add(startField, constraints);
        startField.setFormats("yyyy-MM-dd HH:mm:ss");
        startField.addActionListener(e -> enableGeneration());
        startField.setDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
        // End date
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        settingsPanel.add(new JLabel("End date"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        settingsPanel.add(endField, constraints);
        endField.setFormats("yyyy-MM-dd HH:mm:ss");
        endField.addActionListener(e -> enableGeneration());
        endField.setDate(new Date());
        // Step
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        settingsPanel.add(new JLabel("Step in milliseconds"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        settingsPanel.add(stepField, constraints);
        // Signal settings panel
        tabbedPane.addTab(signalSettingsPanel.getName(), signalSettingsPanel);
        // Preview panel
        final JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createEmptyBorder());
        previewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        previewPanel.setName("Preview");
        tabbedPane.addTab(previewPanel.getName(), previewPanel);
        tabbedPane.addChangeListener(e -> {
            if (Objects.equals(tabbedPane.getSelectedComponent().getName(), previewPanel.getName())) {
                updatePreview(previewPanel);
            }
        });
        // Output settings panel
        final JPanel outputSettingsPanel = new JPanel(new GridBagLayout());
        outputSettingsPanel.setBorder(BorderFactory.createEmptyBorder());
        outputSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputSettingsPanel.setName("Output settings");
        // Line pattern
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        outputSettingsPanel.add(new JLabel("Line pattern"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        linePatternField.setToolTipText("Use ${v} to insert the value.\nUse ${t} to insert the timestamp in milliseconds.\nUse ${d} to insert the date using format 'YYYY-MM-DD HH:mm:ss.SSS'.");
        outputSettingsPanel.add(linePatternField, constraints);
        linePatternField.addActionListener(e -> enableGeneration());
        linePatternField.setText("${t};${v}");
        // Output file
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        outputSettingsPanel.add(new JLabel("Output file"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        outputSettingsPanel.add(outputFileField, constraints);
        outputFileField.addActionListener(e -> enableGeneration());
        // Browse
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2, 2, 2, 2);
        final JButton browseButton = new JButton("Browse");
        outputSettingsPanel.add(browseButton, constraints);
        browseButton.addActionListener(e -> {
            final JFileChooser jfc = new JFileChooser();
            jfc.setDialogTitle("Select the output file");
            jfc.setAcceptAllFileFilterUsed(true);
            final int returnValue = jfc.showSaveDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                outputFileField.setText(jfc.getSelectedFile().getAbsolutePath());
                enableGeneration();
            }
        });
        tabbedPane.addTab(outputSettingsPanel.getName(), outputSettingsPanel);
        // Output
        final Box outputPanel = Box.createVerticalBox();
        final TitledBorder border = BorderFactory.createTitledBorder("Output");
        border.setTitleJustification(TitledBorder.CENTER);
        outputPanel.setBorder(border);
        final JTextPane outputField = new JTextPane();
        outputField.setEditable(false);
        outputField.setComponentPopupMenu(buildPopupMenu(outputField));
        final JScrollPane outputScrollPane = new JScrollPane(outputField);
        outputPanel.add(outputScrollPane);

        try {
            final SwingAppender appender = (SwingAppender) LoggingUtils.getInstance().getAppender("SWING");
            appender.setup(outputScrollPane, outputField);
        } catch (final IllegalAccessException e) {
            LOGGER.error("Cannot retrieve logging appender", e);
        }

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, outputPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(450);
        quitButton.addActionListener((final ActionEvent event) -> exit());
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        configurationButton.addActionListener(e -> openConfigurationDialog());
        generateButton.setEnabled(false);
        generateButton.addActionListener(e -> generate());
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(configurationButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(generateButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(quitButton);
        final BoxLayout boxLayout = new BoxLayout(result.getContentPane(), BoxLayout.Y_AXIS);
        result.getContentPane().setLayout(boxLayout);
        settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        result.getContentPane().add(splitPane);
        splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        result.getContentPane().add(buttonsPanel);
        result.setMinimumSize(new Dimension(600, 800));
        result.setSize(900, 800);
        result.setLocationRelativeTo(null);
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return result;
    }

    /**
     * Open configuration dialog.
     */
    private void openConfigurationDialog() {
        LOGGER.info("Opening configuration dialog");
        final JDialog dialog = new JDialog(frame, "Configuration");
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        // Connection type
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridwidth = 1;
        panel.add(new JLabel("Connection type"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        // Title
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Title"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        final JTextField titleField = new JTextField();
        titleField.setText(configuration.getTitle());
        panel.add(titleField, constraints);
        // Buttons
        final JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
        final JButton applyConfigurationButton = new JButton("Apply configuration");
        applyConfigurationButton.addActionListener((final ActionEvent event) -> {
            LOGGER.debug("Applying configuration");

            try {
                configuration.setTitle(titleField.getText());
                frame.setTitle(configuration.getTitle());
                configuration.validate();
                closeButton.doClick();
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(applyConfigurationButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(closeButton);
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(10, 2, 10, 2);
        constraints.gridwidth = 2;
        panel.add(buttonsPanel, constraints);
        dialog.add(panel);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.pack();
        SwingUtils.getInstance().centerOnScreen(frame, dialog);
        dialog.setVisible(true);
    }

    /**
     * Reads the configuration.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void readConfiguration() throws IOException {
        configurationFile = new File(CONFIGURATION_FILE);

        if (configurationFile.exists()) {
            LOGGER.info("Loading configuration from file: {}", configurationFile.getAbsolutePath());
            propertiesModificationDate = configurationFile.lastModified();

            try (InputStream in = new FileInputStream(configurationFile)) {
                configuration = XmlUtils.getInstance().fromXml(in, Configuration.class);
            } catch (final IOException e) {
                LOGGER.error("Cannot load configuration", e);
            }
        } else {
            LOGGER.info("Loading default configuration");
            propertiesModificationDate = -1;

            try (InputStream in = ClassLoader.getSystemResourceAsStream(configurationFile.getName())) {
                configuration = XmlUtils.getInstance().fromXml(in, Configuration.class);
            } catch (final IOException e) {
                LOGGER.error("Cannot load default configuration", e);
            }
        }

        configuration.validate();
        LOGGER.debug("Configuration: {}", configuration);
    }

    /**
     * Sets the cursor.
     * @param cursor the new cursor
     */
    private void setCursor(final int cursor) {
        frame.setCursor(Cursor.getPredefinedCursor(cursor));
        tabbedPane.setCursor(Cursor.getPredefinedCursor(cursor));
    }

    /**
     * Update time series chart.
     * @param panel the panel
     */
    @SuppressWarnings("boxing")
    private void updatePreview(final JPanel panel) {
        final long step = ((Number) stepField.getValue()).longValue();
        final long end = System.currentTimeMillis();
        final long start = end - 1000 * 10;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating preview using start: {} to: {} using step: {}ms", start, end, step);
        }

        final Generator generator = (Generator) generatorsCombo.getSelectedItem();
        setCursor(Cursor.WAIT_CURSOR);
        enableInputs(false);
        panel.removeAll();
        EventQueue.invokeLater(() -> {
            try {
                if (Number.class.isAssignableFrom(generator.getValueClass())) {
                    final TimeSeriesCollection timeSeries = new TimeSeriesCollection();
                    final TimeSeries series = new TimeSeries("Values");
                    generator.generate(start, end, step, (t, v) -> {
                        series.add(new FixedMillisecond(t), (Number) v);
                    });
                    timeSeries.addSeries(series);
                    final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "time", "value", timeSeries, false, false, false);
                    final ChartPanel chartPanel = new ChartPanel(chart);
                    chartPanel.setBorder(BorderFactory.createEmptyBorder());
                    chartPanel.setName("Preview");
                    panel.add(chartPanel, BorderLayout.CENTER);
                } else if (String.class.isAssignableFrom(generator.getValueClass())) {
                    final JTextPane outputField = new JTextPane();
                    final StyledDocument document = outputField.getStyledDocument();
                    outputField.setEditable(false);
                    generator.generate(start, end, step, (t, v) -> {
                        try {
                            document.insertString(document.getLength(), (String) v + '\n', null);
                        } catch (final BadLocationException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    });
                    outputField.setComponentPopupMenu(buildPopupMenu(outputField));
                    final JScrollPane outputScrollPane = new JScrollPane(outputField);
                    panel.add(outputScrollPane, BorderLayout.CENTER);
                }
            } catch (final IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                enableInputs(true);
                setCursor(Cursor.DEFAULT_CURSOR);
            }
        });
    }
}
