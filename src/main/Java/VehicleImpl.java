/*
 * Copyright © 2015 Copyright(c) Myself and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.practice.vehicle.impl;

import org.opendaylight.yang.gen.v1.org.project.vehicle.rev151202.Vehicle;

public class VehicleImpl implements AutoCloseable {

    public static final InstanceIdentifier<Vehicle> TOASTER_IID = InstanceIdentifier.builder(Vehicle.class).build;
    private static final VINString VEHICLE_VIN_NUMBER = new VINString("0123456ABCDEFG789");
    private static final String VEHICLE_MANUFACTURER = "Skoda";
    private static final String VEHICLE_COLOR = "";
    private static final int VEHICLE_MOTOR_NUMBER = 3302664;

    public VehicleImpl() {

    }

    public void close() throws Exception {

    }
}
