package com.sma.ui.core.jobdetails.msazurevm;

import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.sma.core.OpconException;
import com.sma.core.api.constants.ExitCodeAdvancedConstants;
import com.sma.core.api.constants.SystemConstants;
import com.sma.core.api.interfaces.IPersistentJob;
import com.sma.core.api.interfaces.ISpecificJobProperties;
import com.sma.core.api.job.specific.WindowsJobProperties;
import com.sma.core.api.master.Token;
import com.sma.core.session.ContextID;
import com.sma.core.util.Util;
import com.sma.ui.core.jobdetails.CommandLineTokenizer;
import com.sma.ui.core.jobdetails.JobDetailsHelper;
import com.sma.ui.core.jobdetails.JobUtil;
import com.sma.ui.core.jobdetails.windows.AbstractWindowsSubJobDetailsWidget;
import com.sma.ui.core.messages.IMessageDisplayer;
import com.sma.ui.core.widgets.base.CTabFolder2;
import com.sma.ui.core.widgets.base.ComboItem;
import com.sma.ui.core.widgets.base.ItemCombo;
import com.sma.ui.core.widgets.job.ExitCodeAdvancedWidget;
import com.sma.ui.core.widgets.listeners.ControlTokenSelectorListener;
import com.sma.ui.core.widgets.listeners.DirtyKeyAdapter;
import com.sma.ui.core.widgets.listeners.DirtySelectionAdapter;
import com.sma.ui.core.widgets.validation.ValidationException;
import com.sma.ui.core.widgets.validation.ValidationMessage;

public class MsAzureVmWindowsSubJobDetailsWidget extends AbstractWindowsSubJobDetailsWidget {

	private final static String COMMAND_SUFFIX = SystemConstants.BACK_SLASH + "AzureVM.exe";

	private static final String LOCPATH_NAME = "Location";
	private static final String LOCPATH_TOOLTIP = "The name of a global property that contains the location of the Azure Storage Connector";
	
	private final String RESOURCE_GROUP_NAME = "Resource Group";
	private final String RESOURCE_GROUP_TOOLTIP = "The Azure Resource Group associated with the request";
	private final String VM_NAME = "VM Name";
	private final String VM_TOOLTIP = "The name of the virtual machine to perform the task on";
	private final String TASK_NAME = "Task";
	private final String TASK_TOOLTIP = "The task to perform";
	private final String PRIVATE_IP_PROPERTY_NAME = "IPPVT Property Name";
	private final String PRIVATE_IP_PROPERTY_TOOLTIP = "The fully qualified property name where the machine private IPADR will be saved (global, SI, JI, SSI property types)";
	private final String PUBLIC_IP_PROPERTY_NAME = "IPPUB Property Name";
	private final String PUBLIC_IP_PROPERTY_TOOLTIP = "The fully qualified property name where the machine public IPADR will be saved (global, SI, JI, SSI property types)";

	private final String VM_TYPE_NAME = "Virtual Machine Type";
	private final String VM_TYPE_TOOLTIP = "The type of Virtual Machine to be created";
	private final String REGION_NAME = "Region";
	private final String REGION_TOOLTIP = "The Azure region within which the operation will be performed";
	private final String IMAGE_NAME = "Image Name";
	private final String IMAGE_TOOLTIP = "The name of the image to be used to create the virtual machine";
	private final String IMAGE_SIZE_NAME = "Image Size";
	private final String IMAGE_SIZE_TOOLTIP = "The size of the image to be used to create the virtual machine";
	private final String DISK_SIZE_NAME = "Disk Size (Gb)";
	private final String DISK_SIZE_TOOLTIP = "The size of the disk in gb required for the virtual machine";
	private final String ADMIN_USER_NAME = "Admin Userid";
	private final String ADMIN_USER_TOOLTIP = "The name to use for the administrator userid";
	private final String ADMIN_USER_PASSWORD_NAME = "Admin Userid Password";
	private final String ADMIN_USER_PASSWORD_TOOLTIP = "The password of the administrator user id encrypted using EM encryption tool";
	private final String NETWORK_NAME = "Network";
	private final String NETWORK_TOOLTIP = "The name of an existing network to use, otherwise set to new to create a new network. Format is <network name>/<subnet name>";
	private final String ADDRESS_SPACE_NAME = "Address Space";
	private final String ADDRESS_SPACE_TOOLTIP = "The address space to use when network value is new. Format is 0.0.0.0/0";
	private final String CUSTOM_IMAGE_NAME = "Custom Image";
	private final String CUSTOM_IMAGE_TOOLTIP = "Indicates if the selected Image used to create the virtual machine is a custom image";

	private ContextID contextID = new ContextID("AzureProperty");

	private String[] windowsServers = null;
	private String[] linuxServers = null;
	private String[] serverSizes = null;

	private Composite _mainComposite;
	private Composite _mainInfoComposite;
	private Text _locPathText;

	private CTabFolder2 _tabFolder;
	private CTabItem _vmActionTab;
	private Text _resourceGroupText;
	private Label _taskLabel;
	private ItemCombo _taskItemCombo;
	private Text _virtualMachineText;
	private Text _privateIpAddressPropertyText;
	private Text _publicIpAddressPropertyText;
	
	private Label _vmTypeLabel;
	private ItemCombo _vmTypeItemCombo;
	private Label _regionLabel;
	private Combo _regionCombo;
	private Label _vmImageLabel;
	private Combo _vmImageCombo;
	private Label _vmSizeLabel;
	private Combo _vmSizeCombo;
	private Text _vmNetworkText;
	private Text _vmAddressSpaceText;
	private Button _vmCustomImageCheckBox;
	private Text _diskSizeText;
	private Text _adminUserIdText;
	private Text _adminUserPasswordText;
	
	private CTabItem _failureCriteriaTab;
	private ExitCodeAdvancedWidget _advancedExitCodeWidget;

	public MsAzureVmWindowsSubJobDetailsWidget(Composite parent,
			IMessageDisplayer messageManager, 
			ContextID context) {
		super(parent, messageManager, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = 0;
		layout.horizontalSpacing = layout.verticalSpacing = 0;
		this.setLayout(layout);
		
		this.createPart(this);
		addListeners();
		
		windowsServers = getPropertyValues(MsAzureVmConstants.WINDOWS_SERVERS_PROPERTY_NAME);
		updateWindowsServerList(windowsServers);

		linuxServers = getPropertyValues(MsAzureVmConstants.LINUX_SERVERS_PROPERTY_NAME);
		updateLinuxServerList(linuxServers);

		serverSizes = getPropertyValues(MsAzureVmConstants.SERVER_SIZES_PROPERTY_NAME);
		updateServerSizeList(serverSizes);

	}

	private void createPart(Composite parent) {

		_mainComposite = new Composite(parent, SWT.NONE);
		_mainComposite.setLayout(new GridLayout(1, false));
		_mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		_mainInfoComposite = new Composite(_mainComposite, SWT.NONE);
		_mainInfoComposite.setLayout(new GridLayout(2, false));
		_mainInfoComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		_locPathText = JobUtil.createLabeledText(_mainInfoComposite, LOCPATH_NAME, 0, JobUtil.COLOR_BLUE, JobUtil.COLOR_LIGHT_GREEN, SWT.BORDER, 1);
		_locPathText.setToolTipText(LOCPATH_TOOLTIP);

		_tabFolder = createTabFolder(_mainComposite);
	}

	private CTabFolder2 createTabFolder(Composite parent) {

		_tabFolder = new CTabFolder2(parent, SWT.NONE);
		_tabFolder.setLayout(new GridLayout(1, false));
		_tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		_tabFolder.applyFormStyle();

		// create tabs
		_vmActionTab = createVirtualMachineTab(_tabFolder);
		_failureCriteriaTab = createFailureCriteriaTab(_tabFolder);

		// show the job details tab
		_tabFolder.setSelection(_vmActionTab);

		return _tabFolder;
	}
	
	private CTabItem createVirtualMachineTab(CTabFolder tabFolder) {

		Composite _composite = JobDetailsHelper.createComposite(tabFolder, 1, false);
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Group taskGroup = JobDetailsHelper.createGroup(_composite, SystemConstants.EMPTY_STRING, 2, false);
		taskGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		_resourceGroupText = JobUtil.createLabeledText(taskGroup,RESOURCE_GROUP_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_resourceGroupText.setToolTipText(RESOURCE_GROUP_TOOLTIP);

		_taskLabel = new Label(taskGroup, SWT.TRAIL);
		_taskLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
		_taskLabel.setForeground(JobUtil.COLOR_BLUE);
		_taskLabel.setText(TASK_NAME);

		_taskItemCombo = new ItemCombo(taskGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		_taskItemCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		_taskItemCombo.setItems(Arrays.asList(MsAzureVmConstants.ComboItemDefinitions.TASKS));
		_taskItemCombo.setToolTipText(TASK_TOOLTIP);

		_virtualMachineText = JobUtil.createLabeledText(taskGroup,VM_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_virtualMachineText.setToolTipText(VM_TOOLTIP);
		
		_privateIpAddressPropertyText = JobUtil.createLabeledText(taskGroup,PRIVATE_IP_PROPERTY_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_privateIpAddressPropertyText.setToolTipText(PRIVATE_IP_PROPERTY_TOOLTIP);

		_publicIpAddressPropertyText = JobUtil.createLabeledText(taskGroup,PUBLIC_IP_PROPERTY_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_publicIpAddressPropertyText.setToolTipText(PUBLIC_IP_PROPERTY_TOOLTIP);
		
		Group createGroup = JobDetailsHelper.createGroup(_composite, "Create Virtual Machine options", 2, false);
		createGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));

		_vmTypeLabel = new Label(createGroup, SWT.TRAIL);
		_vmTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
		_vmTypeLabel.setForeground(JobUtil.COLOR_BLUE);
		_vmTypeLabel.setText(VM_TYPE_NAME);

		_vmTypeItemCombo = new ItemCombo(createGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		_vmTypeItemCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		_vmTypeItemCombo.setItems(Arrays.asList(MsAzureVmConstants.ComboItemDefinitions.VM_TYPES));
		_vmTypeItemCombo.setToolTipText(VM_TYPE_TOOLTIP);

		_regionLabel = new Label(createGroup, SWT.TRAIL);
		_regionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
		_regionLabel.setForeground(JobUtil.COLOR_BLUE);
		_regionLabel.setText(REGION_NAME);

		_regionCombo = new Combo(createGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		_regionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		_regionCombo.setItems(MsAzureVmConstants.ComboItemDefinitions.REGIONS);
		_regionCombo.setToolTipText(REGION_TOOLTIP);

		_vmImageLabel = new Label(createGroup, SWT.TRAIL);
		_vmImageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
		_vmImageLabel.setForeground(JobUtil.COLOR_BLUE);
		_vmImageLabel.setText(IMAGE_NAME);

		_vmImageCombo = new Combo(createGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		_vmImageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		_vmImageCombo.setToolTipText(IMAGE_TOOLTIP);
		
		_vmCustomImageCheckBox = new Button(createGroup, SWT.CHECK);
		_vmCustomImageCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 1));
		_vmCustomImageCheckBox.setText(CUSTOM_IMAGE_NAME);
		_vmCustomImageCheckBox.setToolTipText(CUSTOM_IMAGE_TOOLTIP);

		_vmSizeLabel = new Label(createGroup, SWT.TRAIL);
		_vmSizeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
		_vmSizeLabel.setForeground(JobUtil.COLOR_BLUE);
		_vmSizeLabel.setText(IMAGE_SIZE_NAME);

		_vmSizeCombo = new Combo(createGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		_vmSizeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		_vmSizeCombo.setToolTipText(IMAGE_SIZE_TOOLTIP);
		
		_diskSizeText = JobUtil.createLabeledText(createGroup,DISK_SIZE_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_diskSizeText.setToolTipText(DISK_SIZE_TOOLTIP);

		_vmNetworkText = JobUtil.createLabeledText(createGroup,NETWORK_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_vmNetworkText.setToolTipText(NETWORK_TOOLTIP);

		_vmAddressSpaceText = JobUtil.createLabeledText(createGroup,ADDRESS_SPACE_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_vmAddressSpaceText.setToolTipText(ADDRESS_SPACE_TOOLTIP);

		_adminUserIdText = JobUtil.createLabeledText(createGroup,ADMIN_USER_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_adminUserIdText.setToolTipText(ADMIN_USER_TOOLTIP);

		_adminUserPasswordText = JobUtil.createLabeledText(createGroup,ADMIN_USER_PASSWORD_NAME,0,JobUtil.COLOR_BLUE,JobUtil.COLOR_LIGHT_GREEN, SWT.SINGLE | SWT.BORDER, 1);
		_adminUserPasswordText.setToolTipText(ADMIN_USER_PASSWORD_TOOLTIP);
		
		_vmActionTab = JobUtil.createTabItem(tabFolder, _composite, "Virtual Machine");
		
		return _vmActionTab;
	}
	
	private CTabItem createFailureCriteriaTab(CTabFolder tabFolder) {

		Composite failureCriteriaTab = new Composite(tabFolder, SWT.NONE);
		failureCriteriaTab.setLayout(new GridLayout(2, false));

		Group failureCriteriaAdvanced = new Group(failureCriteriaTab, SWT.NONE);
		failureCriteriaAdvanced.setLayout(new GridLayout());
		failureCriteriaAdvanced.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		_advancedExitCodeWidget = new ExitCodeAdvancedWidget(failureCriteriaAdvanced, this.getMessageDisplayer(), ExitCodeAdvancedConstants.MINIMUM_ROWS_TO_DISPLAY,
				ExitCodeAdvancedConstants.MAXIMUM_ROWS_TO_DISPLAY);
		_advancedExitCodeWidget.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		_failureCriteriaTab = JobUtil.createTabItem(tabFolder,	failureCriteriaTab, MsAzureVmConstants.FAILURE_CRITERIA_TAB_NAME);
		return _failureCriteriaTab;
	}

	private void addListeners() {
		_locPathText.addKeyListener(new DirtyKeyAdapter(this));
		_resourceGroupText.addKeyListener(new DirtyKeyAdapter(this));
		_taskItemCombo.addSelectionListener(new DirtySelectionAdapter(this));
		_virtualMachineText.addKeyListener(new DirtyKeyAdapter(this));
		_privateIpAddressPropertyText.addKeyListener(new DirtyKeyAdapter(this));
		_publicIpAddressPropertyText.addKeyListener(new DirtyKeyAdapter(this));
		_regionCombo.addSelectionListener(new DirtySelectionAdapter(this));
		_vmTypeItemCombo.addSelectionListener(new DirtySelectionAdapter(this));
		_vmImageCombo.addSelectionListener(new DirtySelectionAdapter(this));
		_vmSizeCombo.addSelectionListener(new DirtySelectionAdapter(this));
		_diskSizeText.addKeyListener(new DirtyKeyAdapter(this));
		_adminUserIdText.addKeyListener(new DirtyKeyAdapter(this));
		_adminUserPasswordText.addKeyListener(new DirtyKeyAdapter(this));
		_vmNetworkText.addKeyListener(new DirtyKeyAdapter(this));
		_vmAddressSpaceText.addKeyListener(new DirtyKeyAdapter(this));
		_vmCustomImageCheckBox.addSelectionListener(new DirtySelectionAdapter(this));
		
		_vmTypeItemCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				switch (getSelectedVmType()) {

					case windows:
						_vmImageCombo.setItems(windowsServers);
						_vmImageCombo.select(1);
						break;
		
					case linux:
						_vmImageCombo.setItems(linuxServers);
						_vmImageCombo.select(1);
						break;

					default:
						throw new IllegalArgumentException("unknown vmType " + getSelectedVmType());
				}
			}
		});
		
		_taskItemCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				switch (getSelectedTask()) {

					case create:
						_virtualMachineText.setEnabled(true);
						_privateIpAddressPropertyText.setEnabled(true);
						_publicIpAddressPropertyText.setEnabled(true);
						_vmTypeItemCombo.setEnabled(true);
						_regionCombo.setEnabled(true);
						_vmImageCombo.setEnabled(true);
						_vmCustomImageCheckBox.setEnabled(true);
						_vmSizeCombo.setEnabled(true);
						_vmNetworkText.setEnabled(true);
						_vmAddressSpaceText.setEnabled(true);
						_diskSizeText.setEnabled(true);
						_adminUserIdText.setEnabled(true);
						_adminUserPasswordText.setEnabled(true);
						break;
		
					case deallocate:
						_virtualMachineText.setEnabled(true);
						_privateIpAddressPropertyText.setEnabled(false);
						_publicIpAddressPropertyText.setEnabled(false);
						_vmTypeItemCombo.setEnabled(false);
						_regionCombo.setEnabled(false);
						_vmImageCombo.setEnabled(false);
						_vmCustomImageCheckBox.setEnabled(false);
						_vmSizeCombo.setEnabled(false);
						_vmNetworkText.setEnabled(false);
						_vmAddressSpaceText.setEnabled(false);
						_diskSizeText.setEnabled(false);
						_adminUserIdText.setEnabled(false);
						_adminUserPasswordText.setEnabled(false);
						break;

					case delete:
						_virtualMachineText.setEnabled(true);
						_privateIpAddressPropertyText.setEnabled(false);
						_publicIpAddressPropertyText.setEnabled(false);
						_vmTypeItemCombo.setEnabled(false);
						_regionCombo.setEnabled(false);
						_vmImageCombo.setEnabled(false);
						_vmCustomImageCheckBox.setEnabled(false);
						_vmSizeCombo.setEnabled(false);
						_vmNetworkText.setEnabled(false);
						_vmAddressSpaceText.setEnabled(false);
						_diskSizeText.setEnabled(false);
						_adminUserIdText.setEnabled(false);
						_adminUserPasswordText.setEnabled(false);
						break;

					case list:
						_virtualMachineText.setEnabled(false);
						_privateIpAddressPropertyText.setEnabled(false);
						_publicIpAddressPropertyText.setEnabled(false);
						_vmTypeItemCombo.setEnabled(false);
						_regionCombo.setEnabled(false);
						_vmImageCombo.setEnabled(false);
						_vmCustomImageCheckBox.setEnabled(false);
						_vmSizeCombo.setEnabled(false);
						_vmNetworkText.setEnabled(false);
						_vmAddressSpaceText.setEnabled(false);
						_diskSizeText.setEnabled(false);
						_adminUserIdText.setEnabled(false);
						_adminUserPasswordText.setEnabled(false);
						break;

					case poweroff:
						_virtualMachineText.setEnabled(true);
						_privateIpAddressPropertyText.setEnabled(false);
						_publicIpAddressPropertyText.setEnabled(false);
						_vmTypeItemCombo.setEnabled(false);
						_regionCombo.setEnabled(false);
						_vmImageCombo.setEnabled(false);
						_vmCustomImageCheckBox.setEnabled(false);
						_vmSizeCombo.setEnabled(false);
						_vmNetworkText.setEnabled(false);
						_vmAddressSpaceText.setEnabled(false);
						_diskSizeText.setEnabled(false);
						_adminUserIdText.setEnabled(false);
						_adminUserPasswordText.setEnabled(false);
						break;

					case restart:
						_virtualMachineText.setEnabled(true);
						_privateIpAddressPropertyText.setEnabled(true);
						_publicIpAddressPropertyText.setEnabled(true);
						_vmTypeItemCombo.setEnabled(false);
						_regionCombo.setEnabled(false);
						_vmImageCombo.setEnabled(false);
						_vmCustomImageCheckBox.setEnabled(false);
						_vmSizeCombo.setEnabled(false);
						_vmNetworkText.setEnabled(false);
						_vmAddressSpaceText.setEnabled(false);
						_diskSizeText.setEnabled(false);
						_adminUserIdText.setEnabled(false);
						_adminUserPasswordText.setEnabled(false);
						break;

					case start:
						_virtualMachineText.setEnabled(true);
						_privateIpAddressPropertyText.setEnabled(true);
						_publicIpAddressPropertyText.setEnabled(true);
						_vmTypeItemCombo.setEnabled(false);
						_regionCombo.setEnabled(false);
						_vmImageCombo.setEnabled(false);
						_vmCustomImageCheckBox.setEnabled(false);
						_vmSizeCombo.setEnabled(false);
						_vmNetworkText.setEnabled(false);
						_vmAddressSpaceText.setEnabled(false);
						_diskSizeText.setEnabled(false);
						_adminUserIdText.setEnabled(false);
						_adminUserPasswordText.setEnabled(false);
						break;

					default:
						throw new IllegalArgumentException("unknown task " + getSelectedTask());
				}
			}
		});

		_advancedExitCodeWidget.addDirtyListener(this);
		
		new ControlTokenSelectorListener(_locPathText, getContextID());
		new ControlTokenSelectorListener(_privateIpAddressPropertyText, getContextID());
		new ControlTokenSelectorListener(_publicIpAddressPropertyText, getContextID());
		new ControlTokenSelectorListener(_adminUserIdText, getContextID());
		new ControlTokenSelectorListener(_adminUserPasswordText, getContextID());
	
	}	
	
	@Override
	public void setDefaults() {

		setSendDirtyEvents(false);
		_locPathText.setText(MsAzureVmConstants.LOCATION_PATH_TOKEN);
		_resourceGroupText.setText(SystemConstants.EMPTY_STRING);
		_taskItemCombo.setSelection(MsAzureVmEnums.Task.list, true);
		_taskItemCombo.removeItem(new ComboItem ("Unknown", MsAzureVmEnums.Task.unknown));
		_virtualMachineText.setText(SystemConstants.EMPTY_STRING);
		_privateIpAddressPropertyText.setText(SystemConstants.EMPTY_STRING);
		_publicIpAddressPropertyText.setText(SystemConstants.EMPTY_STRING);
		_vmTypeItemCombo.setSelection(MsAzureVmEnums.VmTypes.windows, true);
		_vmTypeItemCombo.removeItem(new ComboItem ("Unknown", MsAzureVmEnums.Task.unknown));
		_regionCombo.select(0);
		_vmImageCombo.setItems(windowsServers);
		_vmImageCombo.select(0);
		_vmCustomImageCheckBox.setSelection(false);
		_vmSizeCombo.setItems(serverSizes);
		_vmSizeCombo.select(0);
		_vmNetworkText.setText("new");
		_vmAddressSpaceText.setText("10.0.0.0/24");
		_diskSizeText.setText(SystemConstants.EMPTY_STRING);
		_adminUserIdText.setText(SystemConstants.EMPTY_STRING);
		_adminUserPasswordText.setText(SystemConstants.EMPTY_STRING);

		_advancedExitCodeWidget.setDefaults();
		
		_virtualMachineText.setEnabled(false);
		_privateIpAddressPropertyText.setEnabled(false);
		_publicIpAddressPropertyText.setEnabled(false);
		_vmTypeItemCombo.setEnabled(false);
		_regionCombo.setEnabled(false);
		_vmImageCombo.setEnabled(false);
		_vmCustomImageCheckBox.setEnabled(false);
		_vmSizeCombo.setEnabled(false);
		_vmNetworkText.setEnabled(false);
		_vmAddressSpaceText.setEnabled(false);
		_diskSizeText.setEnabled(false);
		_adminUserIdText.setEnabled(false);
		_adminUserPasswordText.setEnabled(false);
		
		setSendDirtyEvents(true);
	}	
	
	@Override
	protected String getCommandLine() {
		StringBuilder builder = new StringBuilder();
		MsAzureVmEnums.Task task = getSelectedTask();
		String taskName = task.name();
		builder.append(JobUtil.autoQuote(_locPathText.getText().trim() + COMMAND_SUFFIX, true));
		builder.append(SystemConstants.VERTICAL_TAB);
		// set resource group
		builder.append(SystemConstants.SIGN_MINUS);
		builder.append(MsAzureVmConstants.Arguments.RESOURCE_GROUP_ARGUMENT);
		builder.append(SystemConstants.VERTICAL_TAB);
		builder.append(SystemConstants.QUOTE);
		builder.append(_resourceGroupText.getText());
		builder.append(SystemConstants.QUOTE);
		builder.append(SystemConstants.VERTICAL_TAB);
		builder.append(SystemConstants.SIGN_MINUS);
		builder.append(MsAzureVmConstants.Arguments.TASK_ARGUMENT);
		builder.append(SystemConstants.VERTICAL_TAB);
		builder.append(SystemConstants.QUOTE);
		builder.append(taskName);
		builder.append(SystemConstants.QUOTE);
		builder.append(SystemConstants.VERTICAL_TAB);
		
		switch (getSelectedTask()) {
		
			case create :
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_virtualMachineText.getText());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				if(!_privateIpAddressPropertyText.equals(SystemConstants.EMPTY_STRING)) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
					builder.append(SystemConstants.QUOTE);
					builder.append(_privateIpAddressPropertyText.getText());
					builder.append(SystemConstants.QUOTE);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				if(!_publicIpAddressPropertyText.equals(SystemConstants.EMPTY_STRING)) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
					builder.append(SystemConstants.QUOTE);
					builder.append(_publicIpAddressPropertyText.getText());
					builder.append(SystemConstants.QUOTE);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.REGION_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_regionCombo.getText().trim());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.ATTRIBUTES_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(getAttributes());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				if(_vmCustomImageCheckBox.getSelection()) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.CUSTOM_IMAGE_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				break;
	
			case deallocate :
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_virtualMachineText.getText());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				break;

			case delete :
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_virtualMachineText.getText());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				break;

			case list :
				break;
		
			case poweroff :
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_virtualMachineText.getText());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				break;

			case restart :
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_virtualMachineText.getText());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				if(!_privateIpAddressPropertyText.equals(SystemConstants.EMPTY_STRING)) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
					builder.append(SystemConstants.QUOTE);
					builder.append(_privateIpAddressPropertyText.getText());
					builder.append(SystemConstants.QUOTE);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				if(!_publicIpAddressPropertyText.equals(SystemConstants.EMPTY_STRING)) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
					builder.append(SystemConstants.QUOTE);
					builder.append(_publicIpAddressPropertyText.getText());
					builder.append(SystemConstants.QUOTE);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				break;
		
			case start :
				builder.append(SystemConstants.SIGN_MINUS);
				builder.append(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT);
				builder.append(SystemConstants.VERTICAL_TAB);
				builder.append(SystemConstants.QUOTE);
				builder.append(_virtualMachineText.getText());
				builder.append(SystemConstants.QUOTE);
				builder.append(SystemConstants.VERTICAL_TAB);
				if(!_privateIpAddressPropertyText.equals(SystemConstants.EMPTY_STRING)) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
					builder.append(SystemConstants.QUOTE);
					builder.append(_privateIpAddressPropertyText.getText());
					builder.append(SystemConstants.QUOTE);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				if(!_publicIpAddressPropertyText.equals(SystemConstants.EMPTY_STRING)) {
					builder.append(SystemConstants.SIGN_MINUS);
					builder.append(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT);
					builder.append(SystemConstants.VERTICAL_TAB);
					builder.append(SystemConstants.QUOTE);
					builder.append(_publicIpAddressPropertyText.getText());
					builder.append(SystemConstants.QUOTE);
					builder.append(SystemConstants.VERTICAL_TAB);
				}
				break;
			
			case unknown:
				break;

		}
		return builder.toString();
	}

	private String getAttributes() {
		MsAzureVmEnums.VmTypes vmType = getSelectedVmType();
		StringBuilder builderAttributes = new StringBuilder();
		builderAttributes.append(vmType.name());
		builderAttributes.append(SystemConstants.COMMA);
		builderAttributes.append(_virtualMachineText.getText().trim());
		builderAttributes.append(SystemConstants.COMMA);
		builderAttributes.append(_vmImageCombo.getText().trim());
		builderAttributes.append(SystemConstants.COMMA);
		builderAttributes.append(_vmSizeCombo.getText().trim());
		builderAttributes.append(SystemConstants.COMMA);
		if(_diskSizeText.getText().equals(SystemConstants.EMPTY_STRING)) {
			builderAttributes.append(SystemConstants.SPACE);
		} else {
			builderAttributes.append(_diskSizeText.getText().trim());
		}
		builderAttributes.append(SystemConstants.COMMA);
		builderAttributes.append(_adminUserIdText.getText().trim());
		builderAttributes.append(SystemConstants.COMMA);
		builderAttributes.append(_adminUserPasswordText.getText().trim());
		builderAttributes.append(SystemConstants.COMMA);
		if(_vmNetworkText.getText().equals(SystemConstants.EMPTY_STRING)) {
			builderAttributes.append(SystemConstants.SPACE);
		} else {
			builderAttributes.append(_vmNetworkText.getText().trim());
		}
		builderAttributes.append(SystemConstants.COMMA);
		if(_vmAddressSpaceText.getText().equals(SystemConstants.EMPTY_STRING)) {
			builderAttributes.append(SystemConstants.SPACE);
		} else {
			builderAttributes.append(_vmAddressSpaceText.getText().trim());
		}
		builderAttributes.append(SystemConstants.COMMA);
		return builderAttributes.toString();
	}

	@Override
	protected String getWorkingDirectory() {
		// extract the location property from the commandline and display this is the 
		int endProperty = _locPathText.getText().indexOf(SystemConstants.BACK_SLASH);
		if(endProperty > -1) {
			return _locPathText.getText().substring(1, endProperty);
		} else {
			return _locPathText.getText() + SystemConstants.BACK_SLASH;
		}
	}
	
	@Override
	protected void initializeContents(ISpecificJobProperties jobProperties)
			throws OpconException {
		WindowsJobProperties windowsDetails = (WindowsJobProperties) jobProperties;
		try {
			if(windowsDetails != null) {
				final String commandLine = windowsDetails.getCommandLine();
				// basic check first
				if (!commandLine.contains(COMMAND_SUFFIX)) {
					throw new ParseException(
							MessageFormat.format(MsAzureVmConstants.PARSE_JOB_COMMAND_ERROR,"Azure Storage"));
				}
				parseCommandLine(commandLine);
				_advancedExitCodeWidget.initializeContents();
				if (getInput() != null && getInput().getSpecificJobProperties() != null) {
					try {
						WindowsJobProperties _jobProperties = (WindowsJobProperties) getInput().getSpecificJobProperties();
						_advancedExitCodeWidget.setInput(_jobProperties.getExitCodeAdvancedRows());
					} catch (OpconException e) {
						setErrorMessage("Error initializing user selector " + Util.getCauseError(e));
					}
				}
			} else {
				setDefaults();
			}
		} catch (Exception e) {
			throw new OpconException(e);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void parseCommandLine(String commandLine) throws ParseException {

		// extract the location property from the command line and display this
		commandLine = removeLeadingTrailingDoubleQuotes(commandLine);
		int endProperty = commandLine.indexOf(SystemConstants.BACK_SLASH);
		if (endProperty > -1) {
			_locPathText.setText(commandLine.substring(0, endProperty));
		}
		// remove exe from commandLine
		commandLine = commandLine.replace(MsAzureVmConstants.LOCATION_PATH_TOKEN + COMMAND_SUFFIX, SystemConstants.EMPTY_STRING).trim();

		Options options = new Options();
		options.addOption(MsAzureVmConstants.Arguments.TASK_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.ATTRIBUTES_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.REGION_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.RESOURCE_GROUP_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT, true, SystemConstants.EMPTY_STRING);
		options.addOption(MsAzureVmConstants.Arguments.CUSTOM_IMAGE_ARGUMENT, false, SystemConstants.EMPTY_STRING);
		
		String[] arguments = CommandLineTokenizer.tokenize(commandLine, true);
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, arguments);
		String task = null;
		
		if(cmd.hasOption(MsAzureVmConstants.Arguments.RESOURCE_GROUP_ARGUMENT)) {
			_resourceGroupText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.RESOURCE_GROUP_ARGUMENT)));
		}
		if(cmd.hasOption(MsAzureVmConstants.Arguments.TASK_ARGUMENT)) {
			task = removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.TASK_ARGUMENT));
			_taskItemCombo.setSelection(getTaskfromName(task), true);
			_taskItemCombo.setEnabled(false);
		}
		switch (getSelectedTask()) {
		
			case create:
				if(cmd.hasOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT)) {
					_virtualMachineText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT)) {
					_privateIpAddressPropertyText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT)) {
					_publicIpAddressPropertyText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.REGION_ARGUMENT)) {
					_regionCombo.select(convertRegionDescriptionToIndex(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.REGION_ARGUMENT)))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.CUSTOM_IMAGE_ARGUMENT)) {
					_vmCustomImageCheckBox.setSelection(true);; 
				} else {
					_vmCustomImageCheckBox.setSelection(false);; 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.ATTRIBUTES_ARGUMENT)) {
					setAttributes(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.ATTRIBUTES_ARGUMENT))); 
				}
				_virtualMachineText.setEnabled(true);
				_privateIpAddressPropertyText.setEnabled(true);
				_publicIpAddressPropertyText.setEnabled(true);
				_vmTypeItemCombo.setEnabled(true);
				_regionCombo.setEnabled(true);
				_vmImageCombo.setEnabled(true);
				_vmCustomImageCheckBox.setEnabled(true);
				_vmSizeCombo.setEnabled(true);
				_vmNetworkText.setEnabled(true);
				_vmAddressSpaceText.setEnabled(true);
				_diskSizeText.setEnabled(true);
				_adminUserIdText.setEnabled(true);
				_adminUserPasswordText.setEnabled(true);
				break;
				
			case deallocate:
				if(cmd.hasOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT)) {
					_virtualMachineText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT))); 
				}
				_virtualMachineText.setEnabled(true);
				_privateIpAddressPropertyText.setEnabled(false);
				_publicIpAddressPropertyText.setEnabled(false);
				_vmTypeItemCombo.setEnabled(false);
				_regionCombo.setEnabled(false);
				_vmImageCombo.setEnabled(false);
				_vmCustomImageCheckBox.setEnabled(false);
				_vmSizeCombo.setEnabled(false);
				_vmNetworkText.setEnabled(false);
				_vmAddressSpaceText.setEnabled(false);
				_diskSizeText.setEnabled(false);
				_adminUserIdText.setEnabled(false);
				_adminUserPasswordText.setEnabled(false);
				break;

			case delete:
				if(cmd.hasOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT)) {
					_virtualMachineText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT))); 
				}
				_virtualMachineText.setEnabled(true);
				_privateIpAddressPropertyText.setEnabled(false);
				_publicIpAddressPropertyText.setEnabled(false);
				_vmTypeItemCombo.setEnabled(false);
				_regionCombo.setEnabled(false);
				_vmImageCombo.setEnabled(false);
				_vmCustomImageCheckBox.setEnabled(false);
				_vmSizeCombo.setEnabled(false);
				_vmNetworkText.setEnabled(false);
				_vmAddressSpaceText.setEnabled(false);
				_diskSizeText.setEnabled(false);
				_adminUserIdText.setEnabled(false);
				_adminUserPasswordText.setEnabled(false);
				break;

			case list:
				_virtualMachineText.setEnabled(false);
				_privateIpAddressPropertyText.setEnabled(false);
				_publicIpAddressPropertyText.setEnabled(false);
				_vmTypeItemCombo.setEnabled(false);
				_regionCombo.setEnabled(false);
				_vmImageCombo.setEnabled(false);
				_vmCustomImageCheckBox.setEnabled(false);
				_vmSizeCombo.setEnabled(false);
				_vmNetworkText.setEnabled(false);
				_vmAddressSpaceText.setEnabled(false);
				_diskSizeText.setEnabled(false);
				_adminUserIdText.setEnabled(false);
				_adminUserPasswordText.setEnabled(false);
				break;
				
			case poweroff:
				if(cmd.hasOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT)) {
					_virtualMachineText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT))); 
				}
				_virtualMachineText.setEnabled(true);
				_privateIpAddressPropertyText.setEnabled(false);
				_publicIpAddressPropertyText.setEnabled(false);
				_vmTypeItemCombo.setEnabled(false);
				_regionCombo.setEnabled(false);
				_vmImageCombo.setEnabled(false);
				_vmCustomImageCheckBox.setEnabled(false);
				_vmSizeCombo.setEnabled(false);
				_vmNetworkText.setEnabled(false);
				_vmAddressSpaceText.setEnabled(false);
				_diskSizeText.setEnabled(false);
				_adminUserIdText.setEnabled(false);
				_adminUserPasswordText.setEnabled(false);
				break;

			case restart:
				if(cmd.hasOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT)) {
					_virtualMachineText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT)) {
					_privateIpAddressPropertyText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT)) {
					_publicIpAddressPropertyText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT))); 
				}
				_virtualMachineText.setEnabled(true);
				_privateIpAddressPropertyText.setEnabled(true);
				_publicIpAddressPropertyText.setEnabled(true);
				_vmTypeItemCombo.setEnabled(false);
				_regionCombo.setEnabled(false);
				_vmImageCombo.setEnabled(false);
				_vmCustomImageCheckBox.setEnabled(false);
				_vmSizeCombo.setEnabled(false);
				_vmNetworkText.setEnabled(false);
				_vmAddressSpaceText.setEnabled(false);
				_diskSizeText.setEnabled(false);
				_adminUserIdText.setEnabled(false);
				_adminUserPasswordText.setEnabled(false);
				break;
				
			case start:
				if(cmd.hasOption(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT)) {
					_virtualMachineText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.VIRTUAL_MACHINE_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT)) {
					_privateIpAddressPropertyText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.PRIVATE_IP_ADR_PROPERTY_ARGUMENT))); 
				}
				if(cmd.hasOption(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT)) {
					_publicIpAddressPropertyText.setText(removeLeadingTrailingDoubleQuotes(cmd.getOptionValue(MsAzureVmConstants.Arguments.PUBLIC_IP_ADR_PROPERTY_ARGUMENT))); 
				}
				_virtualMachineText.setEnabled(true);
				_privateIpAddressPropertyText.setEnabled(true);
				_publicIpAddressPropertyText.setEnabled(true);
				_vmTypeItemCombo.setEnabled(false);
				_regionCombo.setEnabled(false);
				_vmImageCombo.setEnabled(false);
				_vmCustomImageCheckBox.setEnabled(false);
				_vmSizeCombo.setEnabled(false);
				_vmNetworkText.setEnabled(false);
				_vmAddressSpaceText.setEnabled(false);
				_diskSizeText.setEnabled(false);
				_adminUserIdText.setEnabled(false);
				_adminUserPasswordText.setEnabled(false);
				break;
				

		}
	}

	private void setAttributes(String values) {

		String [] attributes = tokenizeParameters(values, false, SystemConstants.COMMA);
		MsAzureVmEnums.VmTypes vmtype = getVmTypefromName(attributes[0]);
		_vmTypeItemCombo.setSelection(vmtype, true);
		if(vmtype.name().equals("windows")) {
			_vmImageCombo.setItems(windowsServers);
			_vmSizeCombo.setItems(serverSizes);
		} else {
			_vmImageCombo.setItems(linuxServers);
			_vmSizeCombo.setItems(serverSizes);
		}
		_vmImageCombo.select(convertImageIndex(attributes[2]));
		_vmSizeCombo.select(convertImageSizeIndex(attributes[3]));
		String tdisk = attributes[4];
		if(tdisk.equals(SystemConstants.SPACE)) {
			_diskSizeText.setText(SystemConstants.EMPTY_STRING);
		} else {
			_diskSizeText.setText(tdisk);
		}
		_adminUserIdText.setText(attributes[5]);
		_adminUserPasswordText.setText(attributes[6]);
		String tnetwork = attributes[7];
		if(tnetwork.equals(SystemConstants.SPACE)) {
			_vmNetworkText.setText(SystemConstants.EMPTY_STRING);
		} else {
			_vmNetworkText.setText(tnetwork);
		}
		String taddress = attributes[8];
		if(taddress.equals(SystemConstants.SPACE)) {
			_vmAddressSpaceText.setText(SystemConstants.EMPTY_STRING);
		} else  {
			_vmAddressSpaceText.setText(taddress);
		}
	}

	private int convertRegionDescriptionToIndex(String description) {
		
		int index = 0;
	    String[] items = _regionCombo.getItems();
	    for (String item : items) {
	        if (item.equals(description)) {
	        	return index;
	        }
	        index++;
	    }
	    return index;
	}
	
	private int convertImageIndex(String description) {
		int index = 0;
	    String[] items = _vmImageCombo.getItems();
	    for (String item : items) {
	        if (item.equals(description)) {
	        	return index;
	        }
	        index++;
	    }
	    return index;
	}

	private int convertImageSizeIndex(String description) {
		int index = 0;
	    String[] items = _vmSizeCombo.getItems();
	    for (String item : items) {
	        if (item.equals(description)) {
	        	return index;
	        }
	        index++;
	    }
	    return index;
	}

	private MsAzureVmEnums.Task getSelectedTask() {
		final ComboItem comboItem = _taskItemCombo.getSelectedItem();
		if(comboItem == null) {
			return MsAzureVmEnums.Task.unknown;
		}
		return (MsAzureVmEnums.Task) comboItem.data;
	}

	private MsAzureVmEnums.Task getTaskfromName(String name) {
		MsAzureVmEnums.Task [] tasks = MsAzureVmEnums.Task.values();
		for (int i = 0; i < tasks.length; i++) {
			if (name.equals(tasks[i].name())){
				return tasks[i];
			}
		}
		throw new InvalidParameterException("Function " + name + " Not Found");
	}

	private MsAzureVmEnums.VmTypes getSelectedVmType() {
		final ComboItem comboItem = _vmTypeItemCombo.getSelectedItem();
		if(comboItem == null) {
			return MsAzureVmEnums.VmTypes.unknown;
		}
		return (MsAzureVmEnums.VmTypes) comboItem.data;
	}

	private MsAzureVmEnums.VmTypes getVmTypefromName(String name) {
		MsAzureVmEnums.VmTypes [] vmtypes = MsAzureVmEnums.VmTypes.values();
		for (int i = 0; i < vmtypes.length; i++) {
			if (name.equals(vmtypes[i].name())){
				return vmtypes[i];
			}
		}
		throw new InvalidParameterException("VMType " + name + " Not Found");
	}

	/* (non-Javadoc)
	 * @see com.sma.ui.core.widgets.interfaces.IJobTypeDetailsWidget#doSave(org.eclipse.core.runtime.IProgressMonitor, com.sma.core.api.interfaces.IPersistentJob)
	 */
	public ValidationMessage doSave(IProgressMonitor monitor, IPersistentJob toSave) throws OpconException {
		ValidationMessage msg = super.doSave(monitor, toSave);
		if (msg != null) {
			return msg;
		}

		final WindowsJobProperties winJob = (WindowsJobProperties) toSave.getSpecificJobProperties();
		monitor.beginTask("Windows Job Advanced Failure Criteria Properties", 5);

		winJob.setExitCodeAdvancedOperators(_advancedExitCodeWidget.getExitCodeAdvancedOperators());
		monitor.worked(1);

		winJob.setExitCodeAdvancedValues(_advancedExitCodeWidget.getExitCodeAdvancedValues());
		monitor.worked(1);

		winJob.setExitCodeAdvancedEndValues(_advancedExitCodeWidget.getExitCodeAdvancedEndValues());
		monitor.worked(1);

		winJob.setExitCodeAdvancedResults(_advancedExitCodeWidget.getExitCodeAdvancedResults());
		monitor.worked(1);

		winJob.setExitCodeAdvancedComparators(_advancedExitCodeWidget.getExitCodeAdvancedComparators());
		monitor.worked(1);

		monitor.done();

		return null;
	}
	
	public ValidationMessage checkContents() {
		ValidationMessage message = super.checkContents();
		if (message != null) {
			return message;
		}
		if (getCommandLine().length() > MsAzureVmConstants.COMMAND_LINE_LIMIT) {
			return new ValidationMessage(this,MsAzureVmConstants.TOO_LONG_COMMAND_LINE,
					IMessageProvider.ERROR);
		}
//		if (!(_virtualMachineResourceGroupText.getText().trim().length() > 0)) {
//			return new ValidationMessage(this,MessageFormat.format(MsAzureConstants.TEXTBOX_CANNOT_BE_EMPTY,
//					MsAzureConstants.VIRTUAL_MACHINE_RESOURCE_GROUP_NAME),IMessageProvider.ERROR, _virtualMachineResourceGroupText);
//		}
//		switch (getSelectedFunction()) {
//
//			case operations:
//				if (!(_virtualMachineOperationsVirtualMachineText.getText().trim().length() > 0)) {
//					return new ValidationMessage(this,MessageFormat.format(MsAzureConstants.TEXTBOX_CANNOT_BE_EMPTY,
//							MsAzureConstants.VIRTUAL_MACHINE_OPERATIONS_TASK_VM_NAME),IMessageProvider.ERROR, _virtualMachineOperationsVirtualMachineText);
//				}
//				
//				switch (getSelectedVirtualMachineOperation()) {
//				
//					case create:
//						if (!(_virtualMachineOperationsCreateAdminUserIdText.getText().trim().length() > 0)) {
//							return new ValidationMessage(this,MessageFormat.format(MsAzureConstants.TEXTBOX_CANNOT_BE_EMPTY,
//									MsAzureConstants.VIRTUAL_MACHINE_OPERATIONS_TASK_CREATE_ADMIN_USER_NAME),IMessageProvider.ERROR, _virtualMachineOperationsCreateAdminUserIdText);
//						}
//						if (!(_virtualMachineOperationsCreateAdminUserPasswordText.getText().trim().length() > 0)) {
//							return new ValidationMessage(this,MessageFormat.format(MsAzureConstants.TEXTBOX_CANNOT_BE_EMPTY,
//									MsAzureConstants.VIRTUAL_MACHINE_OPERATIONS_TASK_CREATE_ADMIN_USER_PASSWORD_NAME),IMessageProvider.ERROR, _virtualMachineOperationsCreateAdminUserPasswordText);
//						}
//						break;
//						
//					case delete:
//						break;
//
//					case poweroff:
//						break;
//
//					case restart:
//						break;
//
//					case start:
//						break;
//
//					default:
//						break;
//				}
//				break;
//		
//			case information:
//				
//				switch (getSelectedVirtualMachineInformation()) {
//				
//					case list:
//						break;
//	
//					default:
//						break;
//				}
//				break;
//		
//				
//			default:
//				break;
//		
//		}
		return null;
	}

	@Override
	protected void checkUnhauthorizedFields(ISpecificJobProperties jobProperties)
			throws ValidationException {
		// validation not required
	}

	private String removeLeadingTrailingDoubleQuotes(String input) {
		
		String removed = input.trim();
		if(removed.length() > 2) {
			if (removed.substring(0, 1).equals(SystemConstants.QUOTE)) {
				removed = removed.substring(1, removed.length());
			}
			if (removed.substring(removed.length() - 1, removed.length()).equals(
					SystemConstants.QUOTE)) {
				removed = removed.substring(0, removed.length() - 1);
			}
		} 
		return removed;
	}

	@SuppressWarnings("unchecked")
	private String[] getPropertyValues(String propertyName) {
		
		String[] values = null;
		Expression matchTokenName = ExpressionFactory.matchExp(Token.TOKEN_NAME_PROPERTY, propertyName);
		List<Token> tokens = Token.getObjectByExpression(contextID.getContext(), Token.class, matchTokenName);
		if (tokens.isEmpty()) {
			return null;
		} else {
			values = tokenizeParameters(tokens.get(0).getTokenValue(), false, SystemConstants.COMMA); 
		}
		return values;
	}

	private static String[] tokenizeParameters(String parameters, boolean keepQuote, String delimiter) {
		final char QUOTE = SystemConstants.QUOTE.toCharArray()[0];
		final char BACK_SLASH = SystemConstants.BACK_SLASH.toCharArray()[0];
		char prevChar = 0;
		char currChar = 0;
		StringBuffer sb = new StringBuffer(parameters.length());

		if (!keepQuote) {
			for (int i = 0; i < parameters.length(); i++) {
				if (i > 0) {
					prevChar = parameters.charAt(i - 1);
				}
				currChar = parameters.charAt(i);

				if (currChar != QUOTE || (currChar == QUOTE && prevChar == BACK_SLASH)) {
					sb.append(parameters.charAt(i));
				}
			}

			if (sb.length() > 0) {
				parameters = sb.toString();
			}
		}
		return parameters.split(delimiter);
	}

	private void updateWindowsServerList(String[] servers) {
		
		if(servers == null) {
			windowsServers = MsAzureVmConstants.ComboItemDefinitions.WINDOWS_SERVERS;
		} else {
			windowsServers = servers;
		}
	}
	
	private void updateLinuxServerList(String[] servers) {
		
		if(servers == null) {
			linuxServers = MsAzureVmConstants.ComboItemDefinitions.LINUX_SERVERS;
		} else {
			linuxServers = servers;
		}
		
	}

	private void updateServerSizeList(String[] sizes) {
		
		if(sizes == null) {
			serverSizes = MsAzureVmConstants.ComboItemDefinitions.SERVER_SIZES;
		} else {
			serverSizes = sizes;
		}
		
	}

}
