/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.github.rustdt.ide.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;

import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.project_model.view.IBundleModelElement;
import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.navigator.LangNavigatorLabelProvider;
import melnorme.lang.ide.ui.navigator.NavigatorElementsSwitcher;
import melnorme.lang.tooling.bundle.BundleInfo;

public class RustNavigatorLabelProvider extends LangNavigatorLabelProvider implements IStyledLabelProvider {
	
	public static interface RustNavigatorLabelElementsSwitcher<RET> extends NavigatorElementsSwitcher<RET> {
		
		@Override
		default RET visitFolder(IFolder folder) {
			if(folder.getProjectRelativePath().equals(new Path("src"))) {
				return visitSourceFolder(folder);
			}
			if(folder.getProjectRelativePath().equals(new Path("tests"))) {
				return visitTestsSourceFolder(folder);
			}
			if(folder.getProjectRelativePath().equals(new Path("target"))) {
				return visitBuildOutpuFolder(folder);
			}
			return visitOther2(folder);
		}
		
		@Override
		public abstract RET visitManifestFile(IFile element);
		
		public abstract RET visitSourceFolder(IFolder element);
		
		public abstract RET visitTestsSourceFolder(IFolder element);
		
		public abstract RET visitBuildOutpuFolder(IFolder element);
		
	}
	
	@Override
	protected DefaultGetStyledStringSwitcher getStyledString_switcher() {
		return new RustNavigatorStyledStringSwitcher();
	}
	
	@Override
	protected DefaultGetImageSwitcher getBaseImage_switcher() {
		return new RustNavigatorImageSwitcher();
	}
	
	protected class RustNavigatorStyledStringSwitcher extends DefaultGetStyledStringSwitcher
			implements RustNavigatorLabelElementsSwitcher<StyledString> {
		
		@Override
		public StyledString visitManifestFile(IFile element) {
			BundleInfo bundleInfo = LangCore_Actual.getBundleModel().getBundleInfo(element.getProject());
			if(bundleInfo == null) {
				return null;
			}
			String bundleVersion = bundleInfo.getManifest().getVersion();
			
			StyledString baseString = new StyledString(element.getName());
			return appendBundleRef(baseString, bundleInfo.getCrateName(), bundleVersion);
		}
		
		@Override
		public StyledString visitSourceFolder(IFolder element) {
			return null;
		}
		
		@Override
		public StyledString visitTestsSourceFolder(IFolder element) {
			return null;
		}
		
		@Override
		public StyledString visitBuildOutpuFolder(IFolder element) {
			return null;
		}
		
		@Override
		public StyledString visitBundleElement(IBundleModelElement bundleElement) {
			return new BundleModelGetStyledStringSwitcher() {
				
			}.switchBundleElement(bundleElement);
		}
		
	}
	
	protected class RustNavigatorImageSwitcher extends DefaultGetImageSwitcher
			implements RustNavigatorLabelElementsSwitcher<ImageDescriptor> {
			
		@Override
		public ImageDescriptor visitManifestFile(IFile element) {
			return LangImages.NAV_PackageManifest;
		}
		
		@Override
		public ImageDescriptor visitSourceFolder(IFolder element) {
			return LangImages.NAV_SourceFolder;
		}
		
		@Override
		public ImageDescriptor visitTestsSourceFolder(IFolder element) {
			return LangImages.NAV_SourceFolderTests;
		}
		
		@Override
		public ImageDescriptor visitBuildOutpuFolder(IFolder element) {
			return LangImages.NAV_OutputFolder;
		}
		
		@Override
		public ImageDescriptor visitBundleElement(IBundleModelElement bundleElement) {
			return new BundleModelGetImageSwitcher() {
				
			}.switchBundleElement(bundleElement);
		}
		
	}
	
}