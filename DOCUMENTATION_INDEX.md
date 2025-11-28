# Documentation Index - MUVS Inspection System

**Last Updated:** 2025-11-24

This file serves as a central index to all project documentation.

## 📚 Core Documentation

### 🎯 Start Here

| Document | Description | Audience |
|----------|-------------|----------|
| **[claude.md](./claude.md)** | **Main context guide** - Project architecture, tech stack, commands, progress tracking | Claude & Developers |
| **[README.md](./README.md)** | Project overview and setup instructions | All users |
| **[ROADMAP.md](./ROADMAP.md)** | Complete project roadmap (Phase 1-6) with features and time estimates | Project managers & Developers |

### 📖 Technical References

| Document | Description | When to Read |
|----------|-------------|--------------|
| **[INTEGRATION.md](./INTEGRATION.md)** | Backend-frontend integration technical reference | When working on API integration |
| **[CORS_FIX.md](./CORS_FIX.md)** | CORS configuration issue and resolution | When encountering CORS errors |
| **[PHASE4_TESTING_PLAN.md](./PHASE4_TESTING_PLAN.md)** | Comprehensive testing strategy and infrastructure setup | Phase 4 testing implementation |
| **[AUDIT_TRAIL_TESTING.md](./AUDIT_TRAIL_TESTING.md)** | Detailed Audit Trail feature testing guide | Testing Audit Trail functionality |
| **[.claude/QUICK_REFERENCE.md](./.claude/QUICK_REFERENCE.md)** | Quick commands and troubleshooting | Daily development |

### 📝 Session Logs

| Document | Description | When to Read |
|----------|-------------|--------------|
| **[.claude/README.md](./.claude/README.md)** | Explains .claude folder purpose | First time |
| **[.claude/sessions/]((./.claude/sessions/)** | Detailed session changelogs | When reviewing recent changes |

## 🗂️ Documentation by Purpose

### For Claude Code Assistant

**Order of reading:**
1. `claude.md` - Main context (always read first)
2. `.claude/sessions/[latest].md` - Recent changes
3. `ROADMAP.md` - Feature roadmap
4. Specific docs as needed (INTEGRATION.md, CORS_FIX.md)

### For New Developers

**Onboarding sequence:**
1. `README.md` - Project overview
2. `claude.md` - Architecture and setup
3. `.claude/QUICK_REFERENCE.md` - Commands
4. `ROADMAP.md` - Understand project direction
5. `.claude/sessions/` - Review recent work

### For Project Managers

**Focus areas:**
1. `ROADMAP.md` - Timeline and features
2. `claude.md` (Progress section) - Current status
3. `.claude/sessions/` - Detailed work logs

### For Troubleshooting

1. `.claude/QUICK_REFERENCE.md` - Common issues
2. `CORS_FIX.md` - CORS-specific problems
3. `.claude/sessions/` - Check recent changes
4. `INTEGRATION.md` - API integration issues

## 📋 Document Status

| Document | Status | Last Updated |
|----------|--------|--------------|
| claude.md | ✅ Active | 2025-11-24 |
| README.md | ⚠️ Needs update | Unknown |
| ROADMAP.md | ✅ Active | 2025-11-23 |
| INTEGRATION.md | ✅ Active | 2025-11-23 |
| CORS_FIX.md | ✅ Active | 2025-11-23 |
| PHASE4_TESTING_PLAN.md | ✅ Active | 2025-11-24 |
| AUDIT_TRAIL_TESTING.md | ✅ Active | 2025-11-24 |
| .claude/README.md | ✅ Active | 2025-11-23 |
| .claude/QUICK_REFERENCE.md | ✅ Active | 2025-11-23 |
| .claude/sessions/*.md | ✅ Active | 2025-11-23 |

## 📁 Documentation Structure

```
muvs-inspection-system/
├── README.md                          # Project overview (needs update)
├── DOCUMENTATION_INDEX.md             # This file - index of all docs
├── claude.md                          # ⭐ Main context guide
├── ROADMAP.md                         # Project roadmap
├── INTEGRATION.md                     # Technical integration guide
├── CORS_FIX.md                        # CORS issue documentation
├── PHASE4_TESTING_PLAN.md             # Phase 4 testing strategy
├── AUDIT_TRAIL_TESTING.md             # Audit Trail test cases
└── .claude/                           # Claude-specific docs
    ├── README.md                      # .claude folder explanation
    ├── QUICK_REFERENCE.md             # Quick commands & tips
    ├── sessions/                      # Session changelogs
    │   └── 2025-11-23-rbac-implementation.md
    └── archive/                       # Deprecated docs (future)
```

## 🔍 Quick Navigation

### By Topic

**Authentication & Security:**
- `claude.md` - Authentication section
- `CORS_FIX.md` - CORS configuration
- `.claude/sessions/2025-11-23-rbac-implementation.md` - RBAC details

**API Development:**
- `INTEGRATION.md` - API endpoints and integration
- `claude.md` - API endpoints table
- `.claude/QUICK_REFERENCE.md` - API quick reference

**Frontend Development:**
- `claude.md` - Frontend tech stack
- `INTEGRATION.md` - Component integration
- `.claude/sessions/` - Recent UI changes

**Deployment & Production:**
- `ROADMAP.md` - Phase 5 (Production prep)
- `claude.md` - Configuration section
- `CORS_FIX.md` - Production CORS settings

**Testing:**
- `PHASE4_TESTING_PLAN.md` - Complete testing strategy
- `AUDIT_TRAIL_TESTING.md` - Audit Trail test cases
- `claude.md` - Testing guidelines
- `.claude/QUICK_REFERENCE.md` - Testing checklist
- `.claude/sessions/` - Test results

## 🔄 Keeping Documentation Updated

### When to Update

| Action | Documents to Update |
|--------|---------------------|
| Complete a feature | `claude.md` (progress + changelog) |
| Start new session | Create new file in `.claude/sessions/` |
| End session | Update session file with results |
| Change architecture | `claude.md`, `INTEGRATION.md` |
| Fix critical bug | Create doc in `.claude/` or update existing |
| Complete a phase | `ROADMAP.md`, `claude.md` |

### Documentation Maintenance

- **Daily:** Update session files
- **Weekly:** Review and update `claude.md` progress
- **Monthly:** Archive old session files if needed
- **Per phase:** Update `ROADMAP.md` status

## 📊 Documentation Coverage

| Area | Coverage | Notes |
|------|----------|-------|
| Architecture | ✅ Complete | See `claude.md` |
| API Reference | ✅ Complete | See `INTEGRATION.md` |
| Setup Guide | ⚠️ Partial | README needs update |
| Testing | ✅ Complete | See `claude.md` |
| Deployment | ⏳ Pending | Phase 5 |
| Troubleshooting | ✅ Complete | See QUICK_REFERENCE |
| Change History | ✅ Complete | See `.claude/sessions/` |

## 💡 Documentation Best Practices

1. **Always update `claude.md` first** - It's the source of truth
2. **Create session files** - Track every development session
3. **Document reasoning** - Not just what, but why
4. **Keep it DRY** - Link to other docs instead of duplicating
5. **Use examples** - Code snippets help understanding
6. **Mark status** - Use ✅ ⚠️ ⏳ symbols for quick status
7. **Date everything** - Include last updated date

## 🔗 External Resources

- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **React Docs:** https://react.dev
- **Spring Security:** https://spring.io/projects/spring-security
- **Vite Docs:** https://vitejs.dev

---

## 📝 Notes

- Documentation is versioned with git
- Session logs track AI-assisted development
- All changes by Claude are documented with reasoning
- Documentation is meant to be living - update frequently

**For questions or clarifications, refer to `claude.md` first.**

---

**Maintained by:** Development Team & Claude Code Assistant
**Started:** 2025-11-23
**Status:** ✅ Active Development
