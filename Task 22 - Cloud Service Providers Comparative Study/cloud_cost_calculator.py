#!/usr/bin/env python3
"""
Task 22: Cloud Infrastructure Cost Calculator
Compares monthly estimated costs between AWS, Google Cloud (GCP), and Microsoft Azure
based on custom workload specifications.
"""

import sys

def calculate_costs(vcpus, memory_gb, db_storage_gb, egress_gb):
    # Rates based on standard us-east/us-central regions (On-Demand / Pay-As-You-Use)
    # AWS Rates:
    aws_vcpu_hour = 0.0416
    aws_mem_gb_hour = 0.00456
    aws_compute_monthly = (vcpus * aws_vcpu_hour + memory_gb * aws_mem_gb_hour) * 730
    aws_db_monthly = 55.0 + (db_storage_gb * 0.115)
    aws_network_monthly = max(0, egress_gb - 100) * 0.09
    aws_total = aws_compute_monthly + aws_db_monthly + aws_network_monthly

    # GCP Rates:
    gcp_vcpu_hour = 0.031611
    gcp_mem_gb_hour = 0.004237
    gcp_compute_monthly = (vcpus * gcp_vcpu_hour + memory_gb * gcp_mem_gb_hour) * 730
    gcp_db_monthly = 48.0 + (db_storage_gb * 0.170)
    gcp_network_monthly = max(0, egress_gb - 100) * 0.085
    gcp_total = gcp_compute_monthly + gcp_db_monthly + gcp_network_monthly

    # Azure Rates:
    azure_vcpu_hour = 0.040
    azure_mem_gb_hour = 0.005
    azure_compute_monthly = (vcpus * azure_vcpu_hour + memory_gb * azure_mem_gb_hour) * 730
    azure_db_monthly = 52.0 + (db_storage_gb * 0.125)
    azure_network_monthly = max(0, egress_gb - 100) * 0.087
    azure_total = azure_compute_monthly + azure_db_monthly + azure_network_monthly

    return {
        "AWS": {
            "compute": aws_compute_monthly,
            "database": aws_db_monthly,
            "networking": aws_network_monthly,
            "total": aws_total
        },
        "GCP": {
            "compute": gcp_compute_monthly,
            "database": gcp_db_monthly,
            "networking": gcp_network_monthly,
            "total": gcp_total
        },
        "Azure": {
            "compute": azure_compute_monthly,
            "database": azure_db_monthly,
            "networking": azure_network_monthly,
            "total": azure_total
        }
    }

def print_report(vcpus=4, memory_gb=16, db_storage_gb=100, egress_gb=250):
    print("=" * 65)
    print(" TASK 22: HYPERSCALE CLOUD COST ESTIMATION MATRIX")
    print("=" * 65)
    print(f"Workload Profile:")
    print(f"  - Compute Cores:   {vcpus} vCPUs (730 hrs/month)")
    print(f"  - System Memory:   {memory_gb} GB RAM")
    print(f"  - Database Store:  {db_storage_gb} GB SSD (Managed MySQL/Postgres)")
    print(f"  - Egress Traffic:  {egress_gb} GB/month outbound")
    print("-" * 65)

    costs = calculate_costs(vcpus, memory_gb, db_storage_gb, egress_gb)

    header = f"{'Provider':<10} | {'Compute':<12} | {'Database':<12} | {'Network':<10} | {'Total / Month':<14}"
    print(header)
    print("-" * 65)

    for provider, data in costs.items():
        row = f"{provider:<10} | ${data['compute']:>10.2f} | ${data['database']:>10.2f} | ${data['networking']:>8.2f} | ${data['total']:>12.2f}"
        print(row)

    print("=" * 65)
    cheapest = min(costs.items(), key=lambda x: x[1]['total'])
    print(f"Most Cost-Effective Provider for this workload: {cheapest[0]} (${cheapest[1]['total']:.2f}/mo)")
    print("=" * 65)

if __name__ == "__main__":
    if len(sys.argv) == 5:
        print_report(int(sys.argv[1]), int(sys.argv[2]), int(sys.argv[3]), int(sys.argv[4]))
    else:
        print_report()
