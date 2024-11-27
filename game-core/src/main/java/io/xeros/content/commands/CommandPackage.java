package io.xeros.content.commands;

import io.xeros.model.entity.player.Right;

import java.util.Objects;

public record CommandPackage(String packagePath, Right right) {

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CommandPackage other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$packagePath = this.packagePath();
        final Object other$packagePath = other.packagePath();
        if (!Objects.equals(this$packagePath, other$packagePath)) return false;
        final Object this$right = this.right();
        final Object other$right = other.right();
        return Objects.equals(this$right, other$right);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CommandPackage;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $packagePath = this.packagePath();
        result = result * PRIME + ($packagePath == null ? 43 : $packagePath.hashCode());
        final Object $right = this.right();
        result = result * PRIME + ($right == null ? 43 : $right.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CommandPackage(packagePath=" + this.packagePath() + ", right=" + this.right() + ")";
    }
}
