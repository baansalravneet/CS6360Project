package com.davisbase.commands.output;

public abstract class DDLOutput extends CommandOutput {
    public DDLOutput(boolean status) {
        this.setStatus(status);
    }

    private boolean status;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
