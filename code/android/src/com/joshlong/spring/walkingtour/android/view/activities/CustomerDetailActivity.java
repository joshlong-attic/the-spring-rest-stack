package com.joshlong.spring.walkingtour.android.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.joshlong.spring.walkingtour.android.R;
import com.joshlong.spring.walkingtour.android.async.*;
import com.joshlong.spring.walkingtour.android.model.Customer;
import com.joshlong.spring.walkingtour.android.service.CustomerService;
import com.joshlong.spring.walkingtour.android.view.activities.support.AbstractActivity;

import javax.inject.Inject;

/**
 * Handles displaying the fields for an individual {@link com.joshlong.spring.walkingtour.android.model.Customer}
 *
 * @author Josh Long
 */
public class CustomerDetailActivity extends AbstractActivity {

    // model information
    @Inject
    CustomerService customerService;
    Customer customer;
    Long customerId;
    // references to components
    EditText firstNameEditText, lastNameEditText;
    Button saveButton;
    CustomerDetailActivity self = this;

    protected void loadCustomer(final Long customerId) {
        customerService.getCustomerById(customerId, new AsyncCallback<Customer>() {
            @Override
            public void methodInvocationCompleted(Customer customer) {
                self.customer = customer;
                self.customerId = self.customer.getId();
                self.firstNameEditText.setText(customer.getFirstName());
                self.lastNameEditText.setText(customer.getLastName());
                Log.d(getClass().getName(), "retrieved result: " + customer.toString());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCustomer(this.customerId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.customerId = getIntent().getExtras().getLong("customerId");

        setContentView(R.layout.customer_detail_activity);

        this.firstNameEditText = (EditText) findViewById(R.id.first_name);
        this.lastNameEditText = (EditText) findViewById(R.id.last_name);
        this.saveButton = (Button) findViewById(R.id.save_button);
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    customerService.updateCustomer(
                            customerId,
                            firstNameEditText.getText().toString(),
                            lastNameEditText.getText().toString(), new AsyncCallback<Customer>() {
                        @Override
                        public void methodInvocationCompleted(Customer customer) {
                            Toast.makeText(self, "your changes to record #" + customer.getId() + " have been saved", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        });
    }

}