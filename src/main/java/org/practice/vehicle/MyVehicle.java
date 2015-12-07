/*
 * Copyright © 2015 Copyright(c) Myself and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.practice.vehicle;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.*;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.TransactionStatus;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.org.project.vehicle.rev151202.VINString;
import org.opendaylight.yang.gen.v1.org.project.vehicle.rev151202.Vehicle;
import org.opendaylight.yang.gen.v1.org.project.vehicle.rev151202.VehicleBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

import javax.annotation.Nullable;


public class MyVehicle implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MyVehicle.class);
    public static final InstanceIdentifier<Vehicle> VEHICLE_IID = InstanceIdentifier.builder(Vehicle.class).build();
    private static final VINString VEHICLE_VIN_NUMBER = new VINString("0123456ABCDEFG789");
    private static final String VEHICLE_MANUFACTURER = "Skoda";
    private static final long VEHICLE_MOTOR_NUMBER = 3302664L;

    private DataBroker dataProvider;

    public MyVehicle() {
    }

    private Vehicle buildVehicle (Vehicle.VehicleStatus status) {
        return new VehicleBuilder().setVehicleColor(Vehicle.VehicleColor.BLACK).setVehicleManufacturer(VEHICLE_MANUFACTURER).setVehicleMotorNumber(VEHICLE_MOTOR_NUMBER).setVehicleVINNumber(VEHICLE_VIN_NUMBER).build();
    }

    public void setDataProvider(final DataBroker salDataProvider) {
        dataProvider = salDataProvider;
        setVehicleStatusUp(null);

    }

    public void close() throws Exception {
        if(dataProvider != null) {
            WriteTransaction t = dataProvider.newWriteOnlyTransaction();
            t.delete(LogicalDatastoreType.OPERATIONAL, VEHICLE_IID);
            ListenableFuture<RpcResult<TransactionStatus>> future = (ListenableFuture) t.submit();
            Futures.addCallback(future, new FutureCallback<RpcResult<TransactionStatus>>() {
                @Override
                public void onSuccess(@Nullable RpcResult<TransactionStatus> result) {
                   LOG.info("Deletion of a vehicle: " + result.toString());
                }
                @Override
                public void onFailure(Throwable t) {
                    LOG.error("Deletion failed: " + t.getMessage());
                }
            });
        }
    }

    public void setVehicleStatusUp(final Function<Boolean, Void> resultCallback) {
        WriteTransaction t = dataProvider.newWriteOnlyTransaction();
        t.put(LogicalDatastoreType.OPERATIONAL, VEHICLE_IID, buildVehicle(Vehicle.VehicleStatus.ON));

        ListenableFuture<RpcResult<TransactionStatus>> future = (ListenableFuture) t.submit();
        Futures.addCallback(future, new FutureCallback<RpcResult<TransactionStatus>>() {

            @Override
            public void onSuccess(@Nullable RpcResult<TransactionStatus> result) {
                if(result.getResult() != TransactionStatus.COMMITED) {
                    LOG.error("Failed to establish vehicle object: " + result.getErrors());
                }
                notifyCallback(result.getResult() == TransactionStatus.COMMITED);
            }

            @Override
            public void onFailure(Throwable t) {
                LOG.error("Failed to create vehicle object", t);
            }

            private void notifyCallback(boolean result) {
                if(resultCallback != null) {
                    resultCallback.apply(result);
                }
            }
        });
    }
}
