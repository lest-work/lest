#!/bin/bash
cd "$(dirname "$0")/.." && docker-compose up -d lest-modules-meeting lest-modules-notification lest-modules-ai lest-modules-performance lest-modules-open lest-modules-plugin lest-modules-wakapi
